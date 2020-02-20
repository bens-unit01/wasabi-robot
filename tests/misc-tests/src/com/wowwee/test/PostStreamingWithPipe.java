
package com.wowwee.test;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Pipe;
public class PostStreamingWithPipe extends  Thread{
    public interface Listener {

        /**
         * Called when a new piece of text was recognized by the Speech API.
         *
         * @param text    The text.
         * @param isFinal {@code true} when the API finished processing audio.
         */
        void onSpeechRecognized(String text, boolean isFinal);


        void onError(Throwable t);

        void onCompleted();
    }

    public static final MediaType MEDIA_TYPE_MARKDOWN
            = MediaType.parse("text/x-markdown; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();
    private final PipeBody pipeBody = new PipeBody();
    private Request request;
    private byte[][] buffers;
    private int maxBuffers = 10240;
    private int cntBuffers = 0;
    private boolean running = false;
    private Object mLock = new Object();
    private boolean bStopped = false;
    private final ArrayList<Listener> mListeners = new ArrayList<>();
    
    public void addListener(Listener listener) {
        mListeners.add(listener);
    }

    public void removeListener(Listener listener) {
        mListeners.remove(listener);
    }

    PostStreamingWithPipe() {
        buffers = new byte[maxBuffers][];
    }
    @Override
    public void run() {
        request = new Request.Builder()
                .url("https://labs.godogfetch.com/api/speechToTextStream")
                .addHeader("key","8b34d3432d653f31575f37b3b94247bf")
                .addHeader("secret", "qAK7bDD4bEqQgmral2921ab")
                .addHeader("lexicon", "[]")
                .addHeader("codec", "opus")
                .addHeader("socketid", AppCtrl.SOCKET_ID)
                .post(pipeBody)
                .build();
        streamPrimesToSinkAsynchronously(pipeBody.sink());
        running = true;
		System.out.println("Request -------------- socketID: " + AppCtrl.SOCKET_ID);
        try {
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
				System.out.println("STATUS: " + response.code());
				System.out.println("HEADERS: " + response.headers().toString());
				String resStr = response.body().string();
				System.out.println("BODY: " + resStr);
                for (Listener listener : mListeners) {
                    listener.onSpeechRecognized(resStr, true);
                }
				response.body().close();
            }
        } catch ( Exception e )
        {
        	System.out.println("<PosStreamingWithPipe#run> --- exception -------------------");
            e.printStackTrace();
        }
        running = false;
    }
    public boolean isRunning()
    {
        return running;
    }
    private Thread writeThread = null;
    private void streamPrimesToSinkAsynchronously(final BufferedSink sink) {
        writeThread = new Thread("writer") {
            @Override public void run() {
                try {
                    int i = 0;
                    while(true) {
                    	  Thread.sleep(200);
							while (cntBuffers > i && !bStopped) {
								synchronized (mLock) {
									try{
		                                sink.write(buffers[i++]);
		                                System.out.println("Write Buffer: Count:" + cntBuffers + " Index:" + i);
										if(cntBuffers > 59) {
										     System.out.println(" debug - cntBuffers : " + cntBuffers); 
										}
									}catch( Exception e){
										e.printStackTrace();
									}
								}
                            }
                            if (bStopped) {
								System.out.println("Writing Thread Stopped");
                                break;
							}               
                    }
                    sink.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        writeThread.start();
    }
    public void stopStreaming()
    {
		synchronized (mLock) {
			bStopped = true;
		}
    }
    public void addStreamData(byte[] data, int length)
    {
        synchronized (mLock) {
            try {
                buffers[cntBuffers] = new byte[length];
                System.arraycopy(data, 0, buffers[cntBuffers++], 0, length);
            	System.out.println("Read thread -----" + cntBuffers + " " + length);                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This request body makes it possible for another thread to stream data to the uploading request.
     * This is potentially useful for posting live event streams like video capture. Callers should
     * write to {@code sink()} and close it to complete the post.
     */
    static final class PipeBody extends RequestBody {
//        private final Pipe pipe = new Pipe(8192);
        private final Pipe pipe = new Pipe(8192 * 2);
        private final BufferedSink sink = Okio.buffer(pipe.sink());

        public BufferedSink sink() {
            return sink;
        }

        @Override public MediaType contentType() {
            return MEDIA_TYPE_MARKDOWN;
        }

        @Override public void writeTo(BufferedSink sink) throws IOException {
            sink.writeAll(pipe.source());
        }
    }
}
