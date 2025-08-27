#!/bin/bash
echo "### MVA tomcat start"

if [ "${JAVA_HOME}" = ""  ]; then
    export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.302.b08-0.el7_9.x86_64
    export PATH=$JAVA_HOME/bin:$JAVA_HOME/jre/bin:$PATH
fi

APP_DIR=/data/www/MVA_SP_SDK

cd $APP_DIR

JAVA_OPTS="-Xms2048m -Xmx8192m -XX:MetaspaceSize=512m -XX:MaxMetaspaceSize=2048m"

echo "### APP_DIR    : ${APP_DIR}"
echo "### JAVA_HOME  : ${JAVA_HOME}"
echo "### JAVA_OPTS  : ${JAVA_OPTS}"

nohup java $JAVA_OPTS -jar $APP_DIR/sp-with-sdk.war > /dev/null 2>&1 &

#timeout 5s tail -100f proxy_server.log
echo "### MVA tomcat start complete"
