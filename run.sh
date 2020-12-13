#!/bin/sh

mvn exec:java -Dexec.mainClass="bgu.spl.mics.application.Main" -Dexec.args="$1 $2"
