package com.wowwee.test;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class PostStreamingWithPipTest1 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
		PostStreamingWithPipe streamingPipeThread = null;
		PostStreamingWithPipe.Listener mSheneticsServiceListener =
            new PostStreamingWithPipe.Listener() {
                @Override
                public void onSpeechRecognized(String text, boolean isFinal) {
		            System.out.println("Speech Recognized: " + text);
                }

                @Override
                public void onError(Throwable t) {

		            System.out.println("onError ");
                
                }

                @Override
                public void onCompleted() {
		            System.out.println("onCompleted ... ");
                
                }
            };

        try {
	        streamingPipeThread = new PostStreamingWithPipe();
	        streamingPipeThread.addListener(mSheneticsServiceListener);

            FileInputStream fileIn = new FileInputStream("voice.raw");
            byte[] inBuf = new byte[2048];
			streamingPipeThread.start();
			int i = 0;
            while (fileIn.available() > inBuf.length) {
                int bytesRead = fileIn.read(inBuf, 0, inBuf.length);
                streamingPipeThread.addStreamData(inBuf, bytesRead);
	            //System.out.println("Read buffer "+(i++));
            }
            System.out.println("Stopping Streaming");
			streamingPipeThread.stopStreaming();
//			streamingPipeThread.join();
            fileIn.close();
            //fileOut.close();
            System.out.println("Done!");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
