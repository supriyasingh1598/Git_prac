#!/bin/bash
#
#
#
echo This script will do the following: 
echo   1-Shutdown Tomcat
echo   2-Drop the IIQ Database
echo   3-Remove the IIQ bits
echo   4-Reset the TRAKK and PRISM Databases
echo   5-Reset the LDAP database using LDIF
echo   6-Zero Out Log Files
echo
echo This is not-reversible. 
echo Are you sure you want to continue? 
echo Press [enter] to continue or Ctrl-C to exit
read
#
ShutdownTomcat.sh
#
DropIdentityIQdb.sh
#
RemoveBits.sh
#
ResetTRAKKPRISMdbs.sh
#
ResetLDAP.sh
#
ZeroLogFiles.sh

