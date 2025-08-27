#!/bin/sh

PID=`ps -ef | grep 'sp-with-sdk.war' | grep -v grep | awk '{print $2}'`

if [ "" != "$PID" ]; then
    echo "kill $PID"
    kill -9 $PID
else
    echo "process not running..."    
fi
