/**
 * Copyright 2017, Google, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/**
 * This application demonstrates how to perform basic recognize operations with
 * with the Google Cloud Speech API.
 *
 * For more information, see the README.md under /speech and the documentation
 * at https://cloud.google.com/speech/docs.
 */

'use strict';

function syncRecognize (audio_base64, encoding, sampleRateHertz, languageCode) {
  // [START speech_sync_recognize]
  // Imports the Google Cloud client library
  const fs = require('fs');
  const Speech = require('@google-cloud/speech');

  // Instantiates a client
  const speech = Speech();

  // The path to the local file on which to perform speech recognition, e.g. /path/to/audio.raw
  // const filename = '/path/to/audio.raw';

  // The encoding of the audio file, e.g. 'LINEAR16'
  // const encoding = 'LINEAR16';

  // The sample rate of the audio file in hertz, e.g. 16000
  // const sampleRateHertz = 16000;

  // The BCP-47 language code to use, e.g. 'en-US'
  // const languageCode = 'en-US';

  const config = {
    encoding: encoding,
    sampleRateHertz: sampleRateHertz,
    languageCode: languageCode
  };
  const audio = {
    content: audio_base64 
  };

  const request = {
    config: config,
    audio: audio
  };

  // Detects speech in the audio file
  speech.recognize(request)
    .then((data) => {
      const response = data[0];
      const transcription = response.results.map(result =>
          result.alternatives[0].transcript).join('\n');
      console.log(`Transcription: `, transcription);
    })
    .catch((err) => {
      console.error('ERROR:', err);
    });
  // [END speech_sync_recognize]
}




console.log("app started ..."); 
console.log(" -- " + require.prototype.constructor); 
const opts = {
    encoding: 'LINEAR16',
    sampleRateHertz:  16000,
    languageCode: 'en-US'
  }

		
var server = require('http').createServer();
var io = require('socket.io')(server);

io.on('connection', function(client){
		console.log("on connection");
		//		console.dir(client); 
		//streamingRecognize("file01.wav", opts.encoding, opts.sampleRateHertz, opts.languageCode)

		client.on('event', function(data){
			console.log('on event '); 	
			});
		client.on('disconnect', function(){
			console.log('on disconnect'); 	
			});

		client.on('search', function(arg){
			console.log('on search'); 
			console.dir(arg); 
			});

		client.on('speechToTextStream', function(arg){
			console.log('on speechToTextStream'); 
                        syncRecognize(arg, opts.encoding, opts.sampleRateHertz, opts.languageCode)
			//console.dir(arg); 
			});




		});
console.log("listening on port 3000"); 
server.listen(3000);



 

