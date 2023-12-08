#!/bin/bash
#
CleanUpAll.sh
#
InstallAll.sh
#
InitializeIIQLCM.sh
#
PatchIIQ.sh
#
FastTrack-LoadXML.sh
#
# copy customized iiq startup file to WEB-INF/bin in order to support aggregating LDAP
#
cp ~/ProvisioningTraining/admin/scripts/iiq ~/tomcat/webapps/identityiq/WEB-INF/bin
#
FastTrack-RunTasks.sh
#
# copy customized log4j.properties file
#
cp ~/ProvisioningTraining/config/log4j.properties ~/tomcat/webapps/identityiq/WEB-INF/classes
#
StartTomcat.sh
