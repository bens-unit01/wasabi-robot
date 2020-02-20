/**
 * © Copyright IBM Corporation 2015
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package com.wowwee.audio;

import com.wowwee.dto.SpeechConfiguration;
import com.wowwee.opus.OpusWriter;
//import com.sun.jna.ptr.PointerByReference;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import org.concentus.OpusEncoder;
import org.concentus.OpusApplication;
import org.concentus.OpusException;
import org.concentus.OpusMode;
import org.concentus.OpusSignal;
//import com.wowwee.opus.JNAOpus;
/**
 * Ogg Opus Encoder
 */
public class OggOpusEnc extends OpusWriter implements ISpeechEncoder {
    // Use PROPRIETARY notice if class contains a main() method, otherwise use COPYRIGHT notice.
    public static final String COPYRIGHT_NOTICE = "(c) Copyright IBM Corp. 2015";
    /** Data writer */
    private OpusWriter writer = null;
    /** Opus encoder reference */
//    private PointerByReference opusEncoder;
    private OpusEncoder encoder = null;
    private ShortBuffer encoderBuffer = null;
    private int encoderBufferIndex = 0;
    /**
     * Constructor
     */
    public OggOpusEnc() {}
    /**
     * For WebSocketClient
     * @param uploader
     * @throws IOException
     */
    public void initEncoderWithUploader(IChunkUploader uploader) throws IOException{
        writer = new OpusWriter(uploader);

        encoderBufferIndex = 0;
        encoderBuffer = ShortBuffer.allocate(SpeechConfiguration.FRAME_SIZE);
/*        IntBuffer error = IntBuffer.allocate(4);
        ByteBuffer bitRateLocation = ByteBuffer.allocate(4);        
        this.opusEncoder = JNAOpus.INSTANCE.opus_encoder_create(       
                SpeechConfiguration.SAMPLE_RATE,
                SpeechConfiguration.AUDIO_CHANNELS,
                JNAOpus.OPUS_APPLICATION_VOIP,
                error);        
        bitRateLocation.order(ByteOrder.LITTLE_ENDIAN);
        OggOpusEnc.writeInt(bitRateLocation.array(),0, 64000);        
        JNAOpus.INSTANCE.opus_encoder_ctl(this.opusEncoder, 4002, bitRateLocation );*/
        try {
        	encoder = new OpusEncoder(16000, 1, OpusApplication.OPUS_APPLICATION_VOIP);
            encoder.setForceMode(OpusMode.MODE_SILK_ONLY);
            encoder.setSignalType(OpusSignal.OPUS_SIGNAL_VOICE);
            encoder.setForceChannels(1);
            encoder.setComplexity(0);    
            encoder.setBitrate(24000);
        } catch (Exception e )
        {
        	e.printStackTrace();
        }        
    }
    /**
     * When the encode begins
     */
    @Override
    public void onStart() {
    	encoderBufferIndex = 0;
        writer.writeHeader("encoder=Lavc56.20.100 libopus");
    }
    /**
     * Encode raw audio data into Opus format then call OpusWriter to write the Ogg packet
     *
     * @param rawAudio
     * @return
     * @throws IOException
     */
    public int encodeAndWrite(byte[] rawAudio) throws IOException {
        int uploadedAudioSize = 0;
        ByteArrayInputStream ios = new ByteArrayInputStream(rawAudio);

        byte[] data = new byte[SpeechConfiguration.FRAME_SIZE*2];
        int bufferSize, read;

        while((read = ios.read(data)) > 0){        		   
            bufferSize = read;
            byte[] pcmBuffer = new byte[read];
            System.arraycopy(data, 0, pcmBuffer, 0, read);

            ShortBuffer shortBuffer = ShortBuffer.allocate(bufferSize);
            for (int i = 0; i < read; i += 2) {
                int b1 = pcmBuffer[i] & 0xff;
                int b2 = pcmBuffer[i+1] << 8;
                shortBuffer.put((short) (b1 | b2));
            }
                        
            shortBuffer.flip();
            
            int sampleIndex = 0;

            while ( sampleIndex < bufferSize/2) {
                int lengthToCopy = Math.min( SpeechConfiguration.FRAME_SIZE - this.encoderBufferIndex, bufferSize/2-sampleIndex );
                short[] dst = new short[lengthToCopy];
                shortBuffer.get(dst, sampleIndex, lengthToCopy);
    		    encoderBuffer.put(dst, this.encoderBufferIndex, lengthToCopy);
    		    sampleIndex += lengthToCopy;
    		    encoderBufferIndex += lengthToCopy;
    		    if ( encoderBufferIndex == SpeechConfiguration.FRAME_SIZE ) {
    		    	ByteBuffer opusBuffer = ByteBuffer.allocate(bufferSize);
    		    	encoderBuffer.flip();
//    		    	int opus_encoded = JNAOpus.INSTANCE.opus_encode(this.opusEncoder, encoderBuffer, SpeechConfiguration.FRAME_SIZE, opusBuffer, bufferSize);
    		    	int opus_encoded = 0;
    	            try {
    	            	opus_encoded = encoder.encode(shortBuffer.array(), 0, SpeechConfiguration.FRAME_SIZE, opusBuffer.array(), 0, bufferSize);
    	            }
    	            catch (OpusException e){
    	            	e.printStackTrace();
    	            }
    		    	
    			    encoderBufferIndex = 0;
    	            opusBuffer.position(opus_encoded);
    	            opusBuffer.flip();
    	            byte[] opusData = new byte[opusBuffer.remaining()];
    	            opusBuffer.get(opusData, 0, opusData.length);

    	            if (opus_encoded > 0) {
    	                uploadedAudioSize += opusData.length;
    	                writer.writePacket(opusData, 0, opusData.length);
    	            }    	            
    			}
            }
        }

        ios.close();
        return uploadedAudioSize;
    }
    /**
     * Close writer
     */
    public void close() {
        try {
            writer.close();
//            JNAOpus.INSTANCE.opus_encoder_destroy(this.opusEncoder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
