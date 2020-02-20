PROJECT_PATH=$PWD
echo $PROJECT_PATH
sudo cp  /etc/security/limits.d/audio.conf   /etc/security/limits.d/audio.old
cd  /etc/security/limits.d/
echo "changing security settings in /etc/security/limits.d/audio.conf"
sudo chmod 666  /etc/security/limits.d/audio.conf
sudo echo "@audio   -  rtprio     95" > audio.conf
sudo echo "@audio   -  memlock    300000000" >> audio.conf
sudo cp  $PROJECT_PATH/system.conf /etc/dbus-1/
#sudo reboot
