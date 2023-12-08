#!/bin/bash
echo Copying and Expanding IIQ bits to `$SP_HOME`
cd $INSTALLABLES_HOME
unzip identityiq-$IIQ_VERSION.zip identityiq.war -d $SP_HOME
cd $SP_HOME
jar -xvf identityiq.war
rm identityiq.war
