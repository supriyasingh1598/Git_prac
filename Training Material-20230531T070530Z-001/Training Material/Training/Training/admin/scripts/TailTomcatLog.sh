#!/bin/bash
gnome-terminal -t "Tomcat Standard Out" -x tail -n 5000 -f /home/spadmin/tomcat/logs/catalina.out 
