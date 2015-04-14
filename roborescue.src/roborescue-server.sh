#!/bin/bash


clear
echo "Inicializando Robo Rescue - Server side ..."
echo ""

java 	-cp "dist/roborescue.jar:dist/lib/*" \
	-Xmx512M \
	-DNOSECURITY=true \
        -Djava.rmi.server=$1 \
	conection.ServerWindow

