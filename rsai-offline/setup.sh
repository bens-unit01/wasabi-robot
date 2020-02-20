#!/bin/bash

# Author : raouf Marsh 28th, 2017
# Email  : raouf@wowwee.com 
# This script will install :
#           1- pocketsphinx
#           2- sphinxbase 
#           3- JackD server
#           4- Gstreamer 
#           5- Other dependencies required for the app rsai-offline
#

PROJECT_PATH=$PWD
echo $PROJECT_PATH


echo "Installing all the dependencies for the RSAI offline app ..."  
## uinstalling pulse-audio 
echo "STEP1 -----------------------------------"
sudo apt-get update 
echo "Uninstalling PulseAudio ..."
sudo apt-get --purge remove libpulse-dev 

## installing JackD audio subsystem
echo "STEP2 -----------------------------------"
echo "Installing JackD audio subsystem ..."
sudo apt-get install jackd2 -y


## installing gstreamer 
echo "STEP3 -----------------------------------"
echo "Installing gstreamer..."
sudo apt-get install python-gst0.10 gstreamer-0.10 gstreamer0.10-plugins-good gstreamer0.10-plugins-ugly -y
sudo apt-get install gstreamer0.10-pocketsphinx -y

## installing bison, swig, python-dev
echo "STEP4 -----------------------------------"
echo "Installing bison, swig, python-dev ..."
sudo apt-get install bison -y
sudo apt-get install python-dev -y
sudo apt-get install swig -y



## creating folder for cmusphinx
echo "STEP5 -----------------------------------"
echo "Installing pocketsphinx and basesphinx in ~/cmupocketshpinx folder ..."  
mkdir ~/cmusphinx
cd ~/cmusphinx

## cloning sphinxbase
echo "STEP6 -----------------------------------"
echo "Downloading sphinxbase, please wait ..."
git clone https://github.com/cmusphinx/sphinxbase

echo "Installing sphinxbase ..."
sudo apt-get install libtool -y
cd sphinxbase
chmod +x autogen.sh
chmod +x configure 
./autogen.sh
./configure && make clean && make && sudo make install


## cloning pocketsphinx 
echo "STEP7 -----------------------------------"
echo "Downloading pocktsphinx, please wait ..."
cd ..
git clone https://github.com/cmusphinx/pocketsphinx

echo "Installing pocketsphinx ..."
cd pocketsphinx 
chmod +x autogen.sh
chmod +x configure 
./autogen.sh
./configure && make clean && make && sudo make install

## installing python-pocketsphinx 
echo "STEP8 -----------------------------------"
echo "Installing python-pocketsphinx ..." 
sudo apt-get install python-pocketsphinx -y


## Setting up the environment 
echo "STEP9 -----------------------------------"
echo "Environment setup ..." 
echo "export LD_LIBRARY_PATH=/usr/local/lib" >> ~/.bashrc
echo "export PKG_CONFIG_PATH=/usr/local/lib/pkgconfig" >> ~/.bashrc
echo "export GST_PLUGIN_PATH=/usr/local/lib/gstreamer-0.10" >> ~/.bashrc
echo "export DBUS_SESSION_BUS_ADDRESS=unix:path=/run/dbus/system_bus_socket" >> ~/.bashrc


## setup of environment variables
export LD_LIBRARY_PATH=/usr/local/lib
export PKG_CONFIG_PATH=/usr/local/lib/pkgconfig
export GST_PLUGIN_PATH=/usr/local/lib/gstreamer-0.10
export DBUS_SESSION_BUS_ADDRESS=unix:path=/run/dbus/system_bus_socket


sudo cp  /etc/security/limits.d/audio.conf   /etc/security/limits.d/audio.old
cd  /etc/security/limits.d/
echo "changing security settings in /etc/security/limits.d/audio.conf"
sudo chmod 666  /etc/security/limits.d/audio.conf
sudo echo "@audio   -  rtprio     95" > audio.conf
sudo echo "@audio   -  memlock    300000000" >> audio.conf
sudo cp  $PROJECT_PATH/system.conf /etc/dbus-1/

echo "Setting up the pocketsphinx audio model ... " 
cd  /usr/share/ 
sudo ln -s /usr/local/share/pocketsphinx/ pocketsphinx
cd  /usr/share/pocketsphinx/model/
sudo mkdir hmm
cd hmm
sudo mkdir en_US
cd $PROJECT_PATH
sudo cp -r hub4wsj_sc_8k/ /usr/share/pocketsphinx/model/hmm/en_US/
chmod +x run

echo "Setup completed ..........."







