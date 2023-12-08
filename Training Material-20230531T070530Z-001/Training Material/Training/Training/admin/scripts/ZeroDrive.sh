#!/bin/bash
echo "Cleaning up drive for compaction. "
echo "Yum cleanup. "
yum clean all
echo "Cleaning up /boot" 
cd /boot
cat /dev/zero > zero.fill;sync;sleep 1;sync;rm -f zero.fill
echo "Cleaning up /home" 
cd /home
cat /dev/zero > zero.fill;sync;sleep 1;sync;rm -f zero.fill
echo "Cleaning up /" 
cd /
cat /dev/zero > zero.fill;sync;sleep 1;sync;rm -f zero.fill
