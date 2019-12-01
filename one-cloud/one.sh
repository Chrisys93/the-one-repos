#! /bin/sh
java -Xmx5G -cp .:lib/ECLA.jar:lib/DTNConsoleConnection.jar core.DTNSim $*
