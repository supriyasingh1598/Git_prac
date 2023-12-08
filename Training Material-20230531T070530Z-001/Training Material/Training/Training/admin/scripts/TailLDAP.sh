#!/bin/bash
gnome-terminal -t "LDAP Provisining Log" -x tail -n 5000 -f /home/spadmin/logs/LDAP-plans.log
