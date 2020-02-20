package com.wowwee.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.LineUnavailableException;
import javax.swing.JFrame;




import com.sun.jna.NativeLibrary;

public class PostStreamingWithPipeTest2 {

	
	private static final String TAG = "SHE";
 

    private AudioCapture microphone;
    
private void initializeMicrophone() {

        if (microphone == null) {
            try {
                getMicrophone();
            } catch (LineUnavailableException e) {
                System.out.println("Could not open the microphone line.");
            }
        }
    }

    private void getMicrophone() throws LineUnavailableException {
        microphone =
                AudioCapture.getAudioHardware(new AudioFormat(16000f, 16, 1, true, false), new MicrophoneLineFactory());
    }

    private InputStream getMicrophoneInputStream(
            RecordingRMSListener rmsListener) throws LineUnavailableException, IOException {

        int numberRetries = 1;

            numberRetries = 5;

        for(; numberRetries > 0; numberRetries--) {
            try {
            	InputStream is = microphone.getAudioInputStream(new RecordingStateListener() {
					
					@Override
					public void recordingStarted() {
					  System.out.println("Recording started ...");	
					}
					
					@Override
					public void recordingCompleted(String data) {
					  System.out.println("Recording completed ... data length: " + data.length());


					}

				}, rmsListener, new PostStreamingWithPipe.Listener() {
					
					@Override
					public void onSpeechRecognized(String text, boolean isFinal) {
					      System.out.println("onSpeechRecording  ...");	
					}
					
					@Override
					public void onError(Throwable t) {
					      System.out.println("onError  ...");	
						
					}
					
					@Override
					public void onCompleted() {
					        System.out.println("onCompleted  ...");	
						
					}
				});
            	return is; 
            } catch (LineUnavailableException | IOException e) {
                if (numberRetries == 1) {
                    throw e;
                }
                System.out.println("Could not open the microphone line.");
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e1) {
                    System.out.println("exception:");
                }
            }
        }

        throw new LineUnavailableException();
    }
	
	private void recordAudio() {

		try {
			final InputStream inputStream = getMicrophoneInputStream(new RecordingRMSListener() {

				@Override
				public void rmsChanged(int rms) {
					String g = "";
					for(int i = 0; i < rms; i++) g+= "#"; 
					//System.out.println("<rms> : " + g);
				}
			});
			System.out.println("socketIO.emit  - audio streaming ");
			//socketIO.emit("speechToTextStream", inputStream); 
	
		} catch (LineUnavailableException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

	}

	public PostStreamingWithPipeTest2() {

	    log(TAG, "app init start ..."); 	
	    NativeLibrary.addSearchPath("libvlc", "C:/Program Files/VideoLAN/VLC"); 
	    
	    initializeMicrophone();

  
 
		new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					final BufferedReader br;
					br = new BufferedReader(new InputStreamReader(System.in));
					while (true) {

						System.out.println("Enter something : ");
						String input = br.readLine();

						if ("a01".equals(input)) {
							System.out.println("wakeword detected ...");

							// add your code here ...
						}

						if ("q".equals(input)) {
							System.out.println("Exit!");
							System.exit(0);
						}

						if ("t".equals(input)) {
							System.out.println("what time is it ? ");
						}

						if ("l".equals(input)) {
							System.out.println("where am I ? ");
						}

						if ("u".equals(input)) {
							System.out.println("Uploading audio stream ");
							recordAudio(); 
						}

						System.out.println("input : " + input);
						System.out.println("-----------\n");
					}

				} catch (IOException e) {
					e.printStackTrace();
				} finally {

				}

			}
		}).start();

		log(TAG, "app init end ...");
	}
	public static void main(String[] args) throws Exception {

		new PostStreamingWithPipeTest2();
	}

	
	       
	    public void log(String tag, String message) {
	       System.out.println("<" + tag + ">  " + message);	
	    }

}
