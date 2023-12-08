JAVA_OPTS="-Xms128m -Xmx256m -Dsun.lang.ClassLoader.allowArraySyntax=true"
#
# the following lines add SSL capabilities to the VM
#
JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.trustStore=/home/spadmin/OpenDS/config/truststore"
JAVA_OPTS="$JAVA_OPTS -Djavax.net.ssl.trustStorePassword=thedoorisopen"
