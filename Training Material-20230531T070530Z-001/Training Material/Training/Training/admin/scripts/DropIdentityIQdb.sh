#!/bin/bash
#
#
#
echo Dropping identityiq Database
mysql -u root --password=root < ../ddl/dropidentityiq.mysql
#
#
#
