#!/bin/bash
gnome-terminal -t "IIQ Log" -x tail -n 5000 -f /home/spadmin/logs/iiq_training_rolling.log
