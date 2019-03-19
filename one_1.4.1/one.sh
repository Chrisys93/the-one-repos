#! /bin/sh
java -Xmx1G -cp .:lib/ECLA.jar:lib/DTNConsoleConnection.jar core.DTNSim $*
