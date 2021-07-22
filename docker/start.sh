#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"
cd $DIR/../..
PROJECT_HOME=$PWD
echo "PROJECT_HOME $PROJECT_HOME"

cd $PROJECT_HOME
mvn clean package -Dmaven.test.skip=true
cp -f $PROJECT_HOME/target/GENSHIN_HELPER-*.jar $PROJECT_HOME/docker/GENSHIN_HELPER.jar