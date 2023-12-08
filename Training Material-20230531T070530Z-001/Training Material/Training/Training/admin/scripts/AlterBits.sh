#!/bin/bash
echo Laying down modified hibernate file
cp $TRAINING_HOME/admin/backup/IdentityExtended.hbm.xml $SP_HOME/WEB-INF/classes/sailpoint/object
echo chmod +x iiq
chmod +x $SP_HOME/WEB-INF/bin/iiq
echo running IIQ schema
$SP_HOME/WEB-INF/bin/iiq schema

