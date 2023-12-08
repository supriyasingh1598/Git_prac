#!/bin/bash
#
echo Shutting down Tomcat
ShutdownTomcat.sh
#
echo Deploying Patch Bits
ExpandPatchBits.sh
#
echo Running upgrade DB script
PatchUpgradedb.sh
#
echo Running iiq patch command
$SP_HOME/WEB-INF/bin/iiq patch $IIQ_PATCHVERSION
#
#
#
