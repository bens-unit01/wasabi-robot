import sys;
import os;
from SpeakPythonRecognizer import SpeakPythonRecognizer

def execute(s):
	print s;
	#exec s;

print "Hello master, I am here to serve.";

#utilize a callback function (in this case, execute) for the recognition thread to call
#this callback function must accept 1 string argument
#the second argument will utilize files, in this case houseCommands.fsg
recog = SpeakPythonRecognizer(execute, "houseCommands");

#sets the level of debug output
#1 is the most output, 10 is the least
recog.setDebug(1);

#call this to start the recognition thread and start recognizing speech
while 1:
    
    recog.recognize();
    print "-------------------------------------";

print "exit from main"; 
