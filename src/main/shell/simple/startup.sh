#!/usr/bin/env bash
BASE_ZIP_NAME="${package-name}-${activeProfile}" # 压缩包名称  xx.zip
PACKAGE_NAME="${package-name}" # 启动包名 xx.jar的xx

#固定变量
BASE_PATH=$(cd $(dirname $0)/..; pwd)
BASE_DIR_PATH="${BASE_PATH}" #解压部署磁盘路径

#启动程序
function start()
{
    #进入运行包目录
    cd ${BASE_DIR_PATH}
    #分类启动
    java -jar ${BASE_DIR_PATH}/${PACKAGE_NAME}.jar >${BASE_DIR_PATH}/${PACKAGE_NAME}.out
}
start
