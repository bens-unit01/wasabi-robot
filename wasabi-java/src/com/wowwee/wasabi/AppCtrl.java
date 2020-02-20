package com.wowwee.wasabi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;

import javax.swing.JFrame;

import org.json.JSONException;
import org.json.JSONObject;

import com.wowwee.wakeword.*;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class AppCtrl extends JFrame implements WakeWordDetectedHandler {

	 private static final int WAKE_WORD_AGENT_PORT_NUMBER = 5123; 	
     
	public AppCtrl() {
		super();
	System.out.println("app started ...");
     socketio_test();
	
/*	
	try {
		WakeWordIPCFactory wakeWordIPCFactory = new WakeWordIPCFactory(); 
			WakeWordIPC wakeWordIPC = wakeWordIPCFactory.createWakeWordIPC(this, WAKE_WORD_AGENT_PORT_NUMBER );
			wakeWordIPC.init(); 
		} catch (IOException e) {
			e.printStackTrace();
		} */
	
	 new Thread(new Runnable() {

         @Override
         public void run() {
                 // TODO Auto-generated method stub
        	 

                 try {
       final BufferedReader br ;
                         br = new BufferedReader(new InputStreamReader(System.in));
                         while (true) {

                                 System.out.println("Enter something : ");
                                 String input = br.readLine();

                                 if ("a01".equals(input)) {
                                         System.out.println("wakeword detedted ...");
                                         
                                         // add your code here ...
                                 }
                                 if ("q".equals(input)) {
                                         System.out.println("Exit!");
                                         System.exit(0);
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
		
	}

   private static void socketio_test(){

		Socket socket;
		try {

			IO.Options opts = new IO.Options();
			opts.forceNew = true;
			opts.reconnection = false;

			socket = IO.socket("http://localhost:3000", opts);

			socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

				@Override
				public void call(Object... args) {
					//socket.emit("foo", "hi");
					// socket.disconnect();
				}

			}).on("event", new Emitter.Listener() {

				@Override
				public void call(Object... args) {
				}

			}).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

				@Override
				public void call(Object... args) {
				}

			});
			socket.connect();

			// Receiving an object
			socket.on("echo", new Emitter.Listener() {
				@Override
				public void call(Object... args) {
					JSONObject obj = (JSONObject) args[0];
					System.out.println("on receive echo ...");
				}
			});

			// Sending object
			JSONObject obj = new JSONObject();
			obj.put("hello", "server");
			obj.put("binary", new byte[42]);
			socket.emit("message", obj);

		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	   
   }	
   
    public static void main(String[] args) throws Exception {
    
            new AppCtrl();
    }


@Override
public void onWakeWordDetected() {
   System.out.println("[AppCtrl#onWakeWordDetected]");	
	
}
}
