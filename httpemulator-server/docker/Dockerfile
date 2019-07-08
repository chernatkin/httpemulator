FROM registry.pyn.ru/hh-ubuntu-java:18.04 

ENV LANG="en_US.UTF-8"

ENV JAR="httpemulator-server.jar"
ENV LOG_DIR="/var/log"
# Актуальные настройки указывать в плейбуке
ENV JVM_OPTS=""

ADD httpemulator-server/target/$JAR $JAR

USER nobody

CMD ["sh", "-c", "eval exec java \"$JVM_OPTS\" -jar $JAR >> \"$LOG_DIR/errout.std\" 2>&1"]
