#!/bin/bash
gnome-terminal -t "Email Log" -x tail -n 1000 -f /home/spadmin/logs/iiq_email.log
