#!/bin/bash
gnome-terminal -t "PRISM Provisining Log" -x tail -n 5000 -f /home/spadmin/logs/PRISM-plans.log
