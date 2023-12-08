#!/bin/bash
#
#
#
echo Creating identityiq Database
mysql -u root --password=root < $SP_HOME/WEB-INF/database/create_identityiq_tables.mysql
#mysql -u root --password=root < ~/ProvisioningTraining/admin/ddl/trakk.ddl
#mysql -u root --password=root < ~/ProvisioningTraining/admin/ddl/prism.ddl
#
#
#
