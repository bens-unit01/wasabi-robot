package io.socket.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JFrame;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import com.wowwee.test.AudioPlayer;;
public class AppCtrl extends JFrame {

	private static final int WAKE_WORD_AGENT_PORT_NUMBER = 5123;
	
    private AudioPlayer audioPlayer = null; 	
	private static final String TAG = "SHE";
    private static io.socket.client.Socket socketIO = null;
    private static String userID = "wasabi1@she.ai";
    private static String userPWD = "GoDogLabs123";
    private static Boolean mGreet = false;
//    private static Activity activity;
    private static String userDataString = "";
    private static JSONObject userData;
    private static String mAsrLang = "en-us";
    private static String emitTag = "";
    private static JSONObject emitData = null;
    private static String mSvrResults = "";
    private static String weatherDataString = "";
    private static JSONObject weatherData;
//   private static PHHueSDK phHueSDK = null;
//   private static PHBridge phHueBridge = null;
    private static String mPhoneNumber = "";
    private static String SSID = "";
    private static double gpsLat = 0.0;
    private static double gpsLng = 0.0;
    private static double speed = 0.0;
//    private static MediaPlayer mediaPlayer = null;


	public void AppCtrl_test01() {
//		super();
		System.out.println("app started ...");

		Socket socket;
		try {

			IO.Options opts = new IO.Options();
			opts.forceNew = true;
			opts.reconnection = false;

			socket = IO.socket("http://localhost:3000", opts);

			socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

				@Override
				public void call(Object... args) {
					// socket.emit("foo", "hi");
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
        
		System.out.println("app init end ...");
	}
   
	public AppCtrl() {

	    log(TAG, "app init start ..."); 	
	    
	    login(userID, userPWD, true); 
	    
	    audioPlayer = new AudioPlayer(); 
	    
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

	                                 if ("t".equals(input)) {
	                                         System.out.println("what time is it ? ");
	                                         search("what time is it"); 
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

		new AppCtrl();
	}

	 public void login(String user, String pwd, boolean mGreet) {
	        if (user == null || pwd == null) return;

//	        activity = app;

	        if (socketIO != null) {
	            try {
	                socketIO.disconnect();
	                socketIO = null;
	            } catch (Exception e) {
	            }
	        }

	        userID = user;
	        userPWD = pwd;

	        this.mGreet = mGreet;
	        socketConnect();
	    }
	 
	 
	 
	    private void socketConnect() {
	        try {
	            socketIO = IO.socket("https://api.godogfetch.com");
	        } catch (URISyntaxException e) {
	            throw new RuntimeException(e);
	        }
	        socketIO.on(Socket.EVENT_CONNECT, onConnect);
	        socketIO.on(Socket.EVENT_DISCONNECT,onDisconnect);
	        socketIO.on(Socket.EVENT_CONNECT_ERROR, onError);
	        socketIO.on(Socket.EVENT_CONNECT_TIMEOUT, onError);
	        socketIO.on(Socket.EVENT_PING, onPing);
	        socketIO.on("clientAuthOK", onClientauthok);
	        socketIO.on("clientAuthNOK", onClientauthok);
	        socketIO.on("updateLang", onUpdatelang);
	        socketIO.on("updateCards", onUpdatecards);
	        socketIO.on("updatechat", onUpdatechat);
	        socketIO.on("updateresult", onUpdateresult);
	        socketIO.on("updateweather", onUpdateweather);
	        socketIO.on("setHueLightsOn", onSetHueLightsOn);
	        socketIO.on("setHueLightsOff", onSetHueLightsOff);
	        socketIO.on("setHueLightsDim", onSetHueLightsDim);
	        socketIO.on("setHueLightsRandom", onSetHueLightsRandom);
	        socketIO.on("setHueLightsColor", onSetHueLightsColor);
	        socketIO.on("addCalendarEvent", onAddCalendarEvent);
	        socketIO.on("addAlarmEvent", onAddAlarmEvent);
	        socketIO.on("addTimerEvent", onAddTimerEvent);
	        socketIO.on("audioOut", onAudioout);
	        socketIO.on("ask", onAsk);
	        socketIO.on("call", onCall);
	        socketIO.on("dialsecretcode", onDialsecretcode);
	        socketIO.on("playSong", onPlaySong);
	        socketIO.on("nextTrack", onNextTrack);
	        socketIO.on("prevTrack", onPrevTrack);
	        socketIO.on("stopMusic", onStopMusic);
	        socketIO.on("pauseMusic", onPauseMusic);
	        socketIO.on("playMusic", onPlayMusic);
	        socketIO.on("sendSMS", onSendSMS);
	        socketIO.on("textResponse", onTextResponse);
	        socketIO.on("sendEmail", onSendEmail);
	        socketIO.on("launchURL", onLaunchurl);
	        socketIO.on("control", onControl);
	        socketIO.on("location", onLocation);
	        socketIO.on("joystick", onJoystick);
	        socketIO.on("locationConfig", onLocationConfig);
	        socketIO.on("voiceMailAlert", onVoiceMailAlert);
	        socketIO.on("voiceMailAlertOff", onVoiceMailAlertOff);
	        socketIO.on("pong", onPong);
	        socketIO.on("flashlighton", onFlashlighton);
	        socketIO.on("flashlightoff", onFlashlightoff);

	        if (socketIO != null) socketIO.connect();
	    }
	    
	    public void search(String q) {
	        speakingStop();
	        try {
	       /*     LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
	            if (lm != null) {
	                try {
	                    Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	                    if (location != null) {
	                        gpsLat = location.getLatitude();
	                        gpsLng = location.getLongitude();
	                    }
	                } catch (SecurityException e ) {

	                }
	            }
	            */

	            JSONObject jsonObj = new JSONObject();
	            jsonObj.put("token", userID);
	            jsonObj.put("id", userID);
	            jsonObj.put("pwd", userPWD);
	            jsonObj.put("q", (String) q);
	            jsonObj.put("latlong", String.format("%f,%f", gpsLat, gpsLng));
	            jsonObj.put("speed", String.format("%f", speed));
	            jsonObj.put("os", "android");
	            jsonObj.put("cell", "false");

	            // set current local time YYYYMMDDHHMMSS
	            Calendar c = Calendar.getInstance();
	            SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssz");
	            jsonObj.put("time", df.format(c.getTime()));

	            emit("search", jsonObj);

	            // play waiting
	            /*
	            if (mediaPlayer == null) {
	                mediaPlayer = new MediaPlayer();
	            } else {
	                mediaPlayer.reset();
	            }
	            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
	                public void onPrepared(MediaPlayer mp) {
	                    mp.start();
	                }
	            });
	            mediaPlayer.setLooping(true);
	            mediaPlayer.setDataSource("https://labs.godogfetch.com/audio/moh.wav");
	            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
	            mediaPlayer.prepareAsync();
	            */
	        } catch (Exception e) {
	        }
	    }

	    public void emit(String tag, JSONObject data) {
	        // save emit data in case of reconnect
	        emitTag = tag;
	        emitData = data;
	        try {
	            if (socketIO != null) {
	                socketIO.emit(emitTag, emitData);
	            }
	            else {
	                socketIO.connect();
	            }
	        } catch (Exception e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
	    }

	    public void release() {
	        try {
	            if (socketIO != null) socketIO.disconnect();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        socketIO = null;
	    }

	    public void loginAuth(boolean status) {
	        log(TAG, "loginAuth: " + status);
	    }

	    public void SHEneticsListener(JSONObject data) {
	    }

	    public void setNotificationLight(boolean status) {
	    }

	    public void speakingStart() {
	    }

	    public void speakingStop() {
	    }


	    private Emitter.Listener onConnect = new Emitter.Listener() {
	        @Override
	        public void call(Object... args) {
	            try {
	                log(TAG, "SocketIO Connected");

	                JSONObject jsonObj = new JSONObject();

	                // set credentials
	                jsonObj.put("id", userID);
	                jsonObj.put("pwd", userPWD);
	                jsonObj.put("xid", "");

	                // set lat long
	                double longitude = 0;
	                double latitude = 0;
	             /*   LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
	                if (lm != null) {
	                    try {
	                        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
	                        if (location != null) {
	                            longitude = location.getLongitude();
	                            latitude = location.getLatitude();
	                        }
	                    } catch ( SecurityException e)
	                    {
	                        e.printStackTrace();
	                    }
	                }
	                */
	                jsonObj.put("latlong", String.format("%f,%f", latitude, longitude));

	                // set current local time YYYYMMDDHHMMSS
	                Calendar c = Calendar.getInstance();
	                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmssz");
	                jsonObj.put("time", df.format(c.getTime()));

	                if (mGreet) {
	                    jsonObj.put("greet", "false");
	                } else {
	                    jsonObj.put("greet", "true");
	                }
	                mGreet = true;

	                if (socketIO != null) socketIO.emit("clientAuth", jsonObj);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	    };

	    private Emitter.Listener onClientauthok= new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	            try {
	                log(TAG, "auth success ...");
	                userData = (JSONObject) arguments[0];
	                mAsrLang = userData.getString("lang");
	                loginAuth(true);
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	    };
	    private Emitter.Listener onClientauthnok = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	            try {
	                log(TAG, "auth failed ...");
	                loginAuth(false);
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	    };

	    private Emitter.Listener onUpdatelang = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	            try {
	                userData = (JSONObject) arguments[0];
	                mAsrLang = userData.toString();
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	    };

	    private Emitter.Listener onUpdatecards = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	            try {
	                userData = (JSONObject) arguments[0];
	                loadUrl("javascript:showWelcome('" + userData.getString("firstName") + "')");
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	    };

	    private Emitter.Listener onUpdatechat = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	            try {
	                loadUrl("javascript:updateChat('')");
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	    };

	    private Emitter.Listener onUpdateresult = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	            try {
	                mSvrResults = ((JSONObject) arguments[0]).toString();
	                loadUrl("javascript:updateResult('')");
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	    };

	    private Emitter.Listener onUpdateweather = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	            try {
	                weatherData = ((JSONObject) arguments[0]);
	                weatherDataString = ((JSONObject) arguments[0]).toString();
	                String tempType = ((JSONObject) arguments[1]).toString();
	                loadUrl("javascript:updateWeather('" + tempType + "')");
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	    };

	    private Emitter.Listener onSetHueLightsOn = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {

                 log(TAG, " onSetHueLighsOn"); 

	        	/*
	            try {
	                if (phHueBridge == null) return;
	                String device = ((JSONObject) arguments[0]).toString();
	                PHLightState lightState = new PHLightState();

	                lightState.setOn(true);
	                phHueBridge.setLightStateForDefaultGroup(lightState);
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	            */
	        }
	    };

	    private Emitter.Listener onSetHueLightsOff = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
                 log(TAG, " onSetHueLighsOff"); 
	        	/*
	            try {
	                if (phHueBridge == null) return;
	                String device = ((JSONObject) arguments[0]).toString();
	                PHLightState lightState = new PHLightState();
	                lightState.setOn(false);
	                phHueBridge.setLightStateForDefaultGroup(lightState);
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	            */
	        }
	    };

	    private Emitter.Listener onSetHueLightsDim = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	        	log(TAG, " onSetHueLightDim"); 
	        /*    try {
	                if (phHueBridge == null) return;
	                String data = ((JSONObject) arguments[0]).toString();
	                String device = ((JSONObject) arguments[1]).toString();
	                int bri = 127;
	                if (data != null) {
	                    try {
	                        bri = Integer.parseInt(data);
	                    } catch (Exception e) {
	                    }
	                }

	                if (bri < 0 || bri > 255) bri = 127;

	                PHLightState lightState = new PHLightState();
	                lightState.setOn(true);
	                lightState.setBrightness(bri);  // range 0 - 255
	                phHueBridge.setLightStateForDefaultGroup(lightState);
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	            */
	        }
	    };

	    private Emitter.Listener onSetHueLightsRandom = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	        	log(TAG, " onSetHueLightRandom"); 
	           /* try {
	                if (phHueBridge == null) return;
	                String device = ((JSONObject) arguments[0]).toString();
	                Random rand = new Random();
	                PHLightState lightState = new PHLightState();
	                lightState.setOn(true);
	                lightState.setHue(rand.nextInt(65535));
	                phHueBridge.setLightStateForDefaultGroup(lightState);
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	            */
	        }
	    };

	    private Emitter.Listener onSetHueLightsColor = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	            /*try {
	                if (phHueBridge == null) return;
	                String color = ((JSONObject) arguments[0]).toString();
	                String device = ((JSONObject) arguments[1]).toString();
	                float X = (float) 0.0;
	                float Y = (float) 0.0;
	                if (color != null) {
	                    try {
	                        JSONArray xy = new JSONArray(color);
	                        X = (float) xy.getDouble(0);
	                        Y = (float) xy.getDouble(1);

	                        PHLightState lightState = new PHLightState();
	                        lightState.setOn(true);
	                        lightState.setX(X);
	                        lightState.setY(Y);
	                        phHueBridge.setLightStateForDefaultGroup(lightState);
	                    } catch (Exception e) {
	                    }
	                }
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	            */
	        }
	    };

	    private Emitter.Listener onAddCalendarEvent = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	        }
	    };
	    private Emitter.Listener onAddAlarmEvent = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	        }
	    };

	    private Emitter.Listener onAddTimerEvent = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	        }
	    };

	    private Emitter.Listener onAudioout = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	            try {
	                String remoteFile = arguments[0].toString();
	                log(TAG, " onAudioout arg0: " + remoteFile);
	                
	                audioPlayer.playAudio(remoteFile); 
	                // play audio file
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	    };

	    private Emitter.Listener onAsk = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	            try {
	                String remoteFile = arguments[0].toString();
	                // play audio file, then open microphone when audio file is complete
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	    };

	    private Emitter.Listener onCall = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	            try {
	                mPhoneNumber = arguments[0].toString();
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	    };

	    private Emitter.Listener onDialsecretcode = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	        }
	    };

	    private Emitter.Listener onPlaySong = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	        }
	    };

	    private Emitter.Listener onNextTrack = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	        }
	    };

	    private Emitter.Listener onPrevTrack = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	        }
	    };

	    private Emitter.Listener onStopMusic = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	        }
	    };

	    private Emitter.Listener onPauseMusic = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	        }
	    };

	    private Emitter.Listener onPlayMusic = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	        }
	    };

	    private Emitter.Listener onSendSMS = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	        }
	    };

	    private Emitter.Listener onTextResponse = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	        }
	    };

	    private Emitter.Listener onSendEmail = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	        }
	    };

	    private Emitter.Listener onLaunchurl = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	        }
	    };

	    private Emitter.Listener onLaunchnav = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	        }
	    };

	    private Emitter.Listener onControl = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	            try {
	                String cmd = arguments[0].toString();
	                cmd = cmd.toUpperCase();
	                JSONObject data = new JSONObject();
	                data.put("type", "control");
	                data.put("mode", cmd);
	                SHEneticsListener(data);
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	    };

	    private Emitter.Listener onLocation = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	            try {
	                int id = Integer.parseInt((((JSONObject)arguments[0]).toString()));
	                JSONObject data = new JSONObject();
	                data.put("type", "location");
	                data.put("id", id);
	                SHEneticsListener(data);
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	    };

	    private Emitter.Listener onJoystick = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	            try {
	                int angle = Integer.parseInt(arguments[0].toString());
	                int power = Integer.parseInt(arguments[1].toString());
	                String deviceID = arguments[2].toString();

	                // check if for this device
	                if (!SSID.equals(deviceID)) return;

	                JSONObject data = new JSONObject();
	                data.put("type", "joystick");
	                data.put("angle", angle);
	                data.put("power", power);
	                data.put("deviceID", deviceID);
	                SHEneticsListener(data);
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	    };

	    private Emitter.Listener onLocationConfig = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	        }
	    };

	    private Emitter.Listener onVoiceMailAlert = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	            try {
	                String id = arguments[0].toString();
	                String txt = arguments[1].toString();
	                String wav = arguments[2].toString();
	                setNotificationLight(true);
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	    };

	    private Emitter.Listener onVoiceMailAlertOff = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	            try {
	                setNotificationLight(false);
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	    };

	    private Emitter.Listener onPong = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	            try {
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	    };

	    private Emitter.Listener onPing = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	            try {
	                log(TAG, " onPing ");  
//	                log(TAG, String.format("onMessage: %s", arguments[0].toString()));
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	    };

	    private Emitter.Listener onFlashlighton = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	        }
	    };
	    private Emitter.Listener onFlashlightoff = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	        }
	    };

	    private Emitter.Listener onMessage = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	        }
	    };

	    private Emitter.Listener onDisconnect = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	            try {
	                log(TAG, String.format("Socket Disconnect"));
	            } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	        }
	    };

	    private Emitter.Listener onError = new Emitter.Listener() {
	        @Override
	        public void call(Object... arguments) {
	        }
	    };

	    private void loadUrl(String url) {
	    };
        
	    public void log(String tag, String message) {
	       System.out.println("<" + tag + ">  " + message);	
	    }

}
