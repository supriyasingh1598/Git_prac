echo Zeroing out class log files
cat /dev/null > ~/logs/iiq_email.log
cat /dev/null > ~/logs/iiq_training_rolling.log
cat /dev/null > ~/logs/LDAP-plans.log
cat /dev/null > ~/logs/PRISM-plans.log
rm ~/logs/iiq_training_rolling.log.*
echo Zeroing out OpenDS log files
cat /dev/null > ~/OpenDS/logs/server.out
rm ~/OpenDS/logs/access.*
rm ~/OpenDS/logs/errors.*
rm ~/OpenDS/logs/replication.*
echo Zeroing out Tomcat log files
rm $CATALINA_HOME/logs/catalina.2* 
rm $CATALINA_HOME/logs/manager.2* 
rm $CATALINA_HOME/logs/host-manager.2* 
rm $CATALINA_HOME/logs/localhost.2* 
cat /dev/null > $CATALINA_HOME/logs/catalina.out
