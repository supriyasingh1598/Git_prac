#!/bin/bash
echo Copying and Expanding IIQ patch bits to `$SP_HOME`
cd $INSTALLABLES_HOME
cp identityiq-$IIQ_PATCHVERSION.jar $SP_HOME
cd $SP_HOME
jar -xvf identityiq-$IIQ_PATCHVERSION.jar
rm identityiq-$IIQ_PATCHVERSION.jar
