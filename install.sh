#! /bin/bash

if [[ "$1" == "" ]]
then
	echo "Give target server name as first parameter."
	exit
fi

SERVICE=temperature
APPNAME=temperature
APPNAME_PACKAGE=${APPNAME}-1.0-SNAPSHOT
TARGET_USER=pi
TARGET_SERVER=$1

sbt dist
rm -rf /tmp/${APPNAME_PACKAGE}
mv target/universal/${APPNAME_PACKAGE}.zip /tmp/
unzip /tmp/${APPNAME_PACKAGE}.zip -d /tmp/
rsync -avz --delete /tmp/${APPNAME_PACKAGE} ${TARGET_SERVER}:/tmp/

ssh ${TARGET_SERVER} sudo rm -rf /opt/${APPNAME}
ssh ${TARGET_SERVER} sudo rm -rf /opt/${APPNAME_PACKAGE}
ssh ${TARGET_SERVER} sudo cp -a /tmp/${APPNAME_PACKAGE} /opt/${APPNAME_PACKAGE}
ssh ${TARGET_SERVER} sudo chown -R ${TARGET_USER}:${TARGET_USER} /opt/${APPNAME_PACKAGE}
ssh ${TARGET_SERVER} sudo ln -s /opt/${APPNAME_PACKAGE} /opt/${APPNAME}

scp cacerts ${TARGET_SERVER}:
ssh ${TARGET_SERVER} sudo mv /home/pi/cacerts /usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt/jre/lib/security/
ssh ${TARGET_SERVER} sudo chown root:root /usr/lib/jvm/jdk-8-oracle-arm32-vfp-hflt/jre/lib/security/cacerts

scp conf/application-$1.conf ${TARGET_SERVER}:/opt/temperature/conf/application.conf

# UPSTART:
#ssh ${TARGET_SERVER} sudo restart ${SERVICE}

# SYSTEMD:
scp temperature.service ${TARGET_SERVER}:
ssh ${TARGET_SERVER} sudo mv /home/pi/temperature.service /etc/systemd/system/
ssh ${TARGET_SERVER} sudo systemctl daemon-reload
ssh ${TARGET_SERVER} sudo systemctl restart ${SERVICE}
