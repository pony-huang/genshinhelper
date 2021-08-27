#!/bin/sh

error_exit ()
{
    echo "ERROR: $1 !!"
    exit 1
}
[ ! -e "$JAVA_HOME/bin/java" ] && JAVA_HOME=$HOME/jdk/java
[ ! -e "$JAVA_HOME/bin/java" ] && JAVA_HOME=/usr/java
[ ! -e "$JAVA_HOME/bin/java" ] && unset JAVA_HOME

if [ -z "$JAVA_HOME" ]; then

  JAVA_PATH=`dirname $(readlink -f $(which javac))`
  if [ "x$JAVA_PATH" != "x" ]; then
    export JAVA_HOME=`dirname $JAVA_PATH 2>/dev/null`
  fi
  if [ -z "$JAVA_HOME" ]; then
        error_exit "Please set the JAVA_HOME variable in your environment, We need java(x64)! jdk8 or later is better!"
  fi
fi

export JAVA_HOME
export JAVA="$JAVA_HOME/bin/java"
export BASE_DIR=`cd $(dirname $0)/..; pwd`
export SERVER="${package-name}" # 脚本自动注入

JAVA_OPT="${JAVA_OPT} -Xms128m -Xmx256m -Xmn128m"
JAVA_OPT="${JAVA_OPT} -Dgenshin.logger=${BASE_DIR}/conf/log4j2.xml"
JAVA_OPT="${JAVA_OPT} -Dgenshin.config=${BASE_DIR}/conf/config.yaml"
JAVA_OPT="${JAVA_OPT} -jar ${BASE_DIR}/${SERVER}.jar"
JAVA_OPT="${JAVA_OPT} ${JAVA_OPT_EXT}"

if [ ! -d "${BASE_DIR}/logs" ]; then
  mkdir ${BASE_DIR}/logs
fi

echo "$JAVA ${JAVA_OPT}"

# check the start.out log output file
if [ ! -f "${BASE_DIR}/logs/startup.out" ]; then
  touch "${BASE_DIR}/logs/startup.out"
fi
# start
echo "$JAVA ${JAVA_OPT}" > ${BASE_DIR}/logs/startup.out 2>&1 &
nohup $JAVA ${JAVA_OPT} >> ${BASE_DIR}/logs/startup.out 2>&1 &
echo "原神签到启动成功,日志目录：${BASE_DIR}/logs/startup.out"
