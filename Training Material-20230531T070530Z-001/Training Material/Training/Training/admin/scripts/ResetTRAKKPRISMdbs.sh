#!/bin/bash
#
#
#
echo Resetting built in databases
mysql -u root --password=root < ~/ProvisioningTraining/admin/ddl/trakk.ddl
mysql -u root --password=root < ~/ProvisioningTraining/admin/ddl/prism.ddl
#
#
