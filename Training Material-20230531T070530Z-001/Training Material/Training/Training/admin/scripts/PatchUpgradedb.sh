#!/bin/bash
#
#
#
echo Upgrade Identityiq Database
mysql -u root --password=root < $SP_HOME/WEB-INF/database/upgrade_identityiq_tables-$IIQ_PATCHVERSION.mysql
#
#
#
