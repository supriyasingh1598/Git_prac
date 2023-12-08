echo Reset LDAP to starting point
~/OpenDS/bin/stop-ds
~/OpenDS/bin/import-ldif -r -l ~/ProvisioningTraining/admin/scripts/ProvisioningTraining-StartingPoint.ldif -n userRoot
~/OpenDS/bin/start-ds
