FROM registry.pyn.ru/openjdk15-production:2021.05.26

ENV LANG="en_US.UTF-8"

ENV JAR="httpemulator.jar"
ENV LOG_DIR="/var/log"
# Актуальные настройки указывать в плейбуке
ENV JVM_OPTS=""
RUN mkdir -p /etc/httpemulator
RUN mkdir -p $LOG_DIR || true && chown -R nobody $LOG_DIR

ADD httpemulator-server/target/$JAR $JAR
ADD httpemulator-server/src/etc/service.properties /etc/httpemulator/

USER nobody

CMD ["sh", "-c", "eval exec java -DsettingsDir=/etc/httpemulator \"$JVM_OPTS\" -jar $JAR >> \"$LOG_DIR/errout.std\" 2>&1"]
