/** 
 * Copyright 2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Amazon Software License (the "License"). You may not use this file 
 * except in compliance with the License. A copy of the License is located at
 *
 *   http://aws.amazon.com/asl/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, express or implied. See the License for the 
 * specific language governing permissions and limitations under the License.
 */
package com.wowwee.test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.TargetDataLine;
import com.wowwee.audio.IChunkUploader;
import com.wowwee.ISpeechDelegate;
import com.wowwee.audio.ISpeechEncoder;
import com.wowwee.audio.OggOpusEnc;

public class AudioCapture{
    private static AudioCapture sAudioCapture;
    private final TargetDataLine microphoneLine;
    private AudioFormat audioFormat;
    private AudioBufferThread thread;
    private static final int BUFFER_SIZE_IN_SECONDS = 6;
    private static final int MAX_REC_ATTEMPTS = 500;
    private static int counterMic = 0; 
    private final int BUFFER_SIZE_IN_BYTES;
    private ISpeechDelegate delegate = null;

    
    public void setDelegate(ISpeechDelegate val) {
        this.delegate = val;
    }
    
    private void log(String message) {
     System.out.println(message);	
    } 
    public static AudioCapture getAudioHardware(final AudioFormat audioFormat,
            MicrophoneLineFactory microphoneLineFactory) throws LineUnavailableException {
        if (sAudioCapture == null) {
            sAudioCapture = new AudioCapture(audioFormat, microphoneLineFactory);
        }
        return sAudioCapture;
    }

    private AudioCapture(final AudioFormat audioFormat, MicrophoneLineFactory microphoneLineFactory)
            throws LineUnavailableException {
        super();
        this.audioFormat = audioFormat;
        microphoneLine = microphoneLineFactory.getMicrophone();
        if (microphoneLine == null) {
            throw new LineUnavailableException();
        }
        BUFFER_SIZE_IN_BYTES = (int) ((audioFormat.getSampleSizeInBits() * audioFormat.getSampleRate()) / 8
                * BUFFER_SIZE_IN_SECONDS);
    }

    public InputStream getAudioInputStream(final RecordingStateListener stateListener,
            final RecordingRMSListener rmsListener, PostStreamingWithPipe.Listener sheneticsServiceListener) throws LineUnavailableException, IOException {
        try {
            startCapture();
            PipedInputStream inputStream = new PipedInputStream(BUFFER_SIZE_IN_BYTES);

            thread = new AudioBufferThread(inputStream, stateListener, rmsListener, sheneticsServiceListener);
            thread.start();
            return inputStream;
        } catch (LineUnavailableException | IOException e) {
            stopCapture();
            throw e;
        }
    }

    public void stopCapture() {
        microphoneLine.stop();
        microphoneLine.close();

    }

    private void startCapture() throws LineUnavailableException {
        microphoneLine.open(audioFormat);
        microphoneLine.start();
    }

    public int getAudioBufferSizeInBytes() {
        return BUFFER_SIZE_IN_BYTES;
    }

    private class AudioBufferThread extends Thread implements IChunkUploader {

        private final AudioStateOutputStream audioStateOutputStream;
        private final PostStreamingWithPipe streamingPipeThread;
        //private final FileOutputStream os;
        private RecordingStateListener stateListener;  
        private final byte[] buf = new byte[160000]; 
        private ISpeechEncoder encoder = null;
        public AudioBufferThread(PipedInputStream inputStream,
                RecordingStateListener recordingStateListener, RecordingRMSListener rmsListener, PostStreamingWithPipe.Listener sheneticsServiceListener)
                        throws IOException {
            audioStateOutputStream =
                    new AudioStateOutputStream(inputStream, recordingStateListener, rmsListener);
            streamingPipeThread = new PostStreamingWithPipe();
	        streamingPipeThread.addListener(sheneticsServiceListener);
	        streamingPipeThread.start(); 

	        this.encoder = new OggOpusEnc();
	        this.encoder.initEncoderWithUploader(this);
	        this.encoder.onStart();
	    //    os = new FileOutputStream("f2"); 
	        this.stateListener = recordingStateListener;
        }

        @Override
        public void run() {
        	int attempts = 0; 
//            while (microphoneLine.isOpen() && attempts < MAX_REC_ATTEMPTS) {
            while (microphoneLine.isOpen() && attempts < MAX_REC_ATTEMPTS) {
            	System.out.println("<AudioCapture#run counterMic " + counterMic++);  
            	if(counterMic > 59)
                copyAudioBytesFromInputToOutput(); 
                attempts++; 
            }
            log("<AudioCapture#run> streaming end ..."); 
            closePipedOutputStream();
        }

        private void copyAudioBytesFromInputToOutput() {
            byte[] data = new byte[microphoneLine.getBufferSize()/5];
            int numBytesRead = microphoneLine.read(data, 0, data.length);

            // convert to an array of bytes and send it to the server
            ByteBuffer bufferBytes = ByteBuffer.allocate(numBytesRead);
            bufferBytes.order(ByteOrder.LITTLE_ENDIAN);
            bufferBytes.put(data,0,numBytesRead);
            byte[] bytes = bufferBytes.array();
            int length = bytes.length;
            int uploadedAudioSize = 0;
            try {
            	uploadedAudioSize = encoder.encodeAndWrite(bytes);
//------	
            } catch (IOException e) {
            	e.printStackTrace();
            }
        }

        public void closePipedOutputStream() {
            try {
                audioStateOutputStream.close();
                streamingPipeThread.stopStreaming();
                stateListener.recordingCompleted(" ");
                encoder.close();
            } catch (IOException e) {
                log("Failed to close audio stream ");
            }
        }
        
        /**
         * Write data into socket
         *
         * @param data
         */
        public void upload(byte[] data){
          try {
              streamingPipeThread.addStreamData(data, data.length);
              System.out.println("<AudioCapture#copyAudioBytesFromInput..> buffer size : " + microphoneLine.getBufferSize()); 
           //   System.arraycopy(data, 0, buf, counter, data.length); 
          } catch (Exception e) {
          	 e.printStackTrace();
              stopCapture();
          }
        }
        /**
         * Stop by sending out zero byte of data
         */
        public void stopUpload(){
            byte[] stopData = new byte[0];
            this.upload(stopData);
        }        
    }
}
