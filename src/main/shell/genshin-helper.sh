#!/usr/bin/env bash
#可变参数变量
languageType="javac" #支持 java,javac 发布
#参数值由pom文件传递
BASE_ZIP_NAME="${package-name}-${activeProfile}" #压缩包名称  publish-test.zip的publish
PACKAGE_NAME="${package-name}" #命令启动包名 xx.jar的xx
MAIN_CLASS="${boot-main}" #java -cp启动时，指定main入口类;命令：java -cp conf;lib\*.jar;${PACKAGE_NAME}.jar ${MAIN_CLASS}

#例子
# BASE_ZIP_NAME="publish-test" #压缩包名称  publish-test.zip的publish
# PACKAGE_NAME="publish" #命令启动包名 publish.jar的xx

#固定变量
BASE_PATH=$(cd $(dirname $0)/..; pwd)
BASE_DIR_PATH="${BASE_PATH}" #解压部署磁盘路径
pid= #进程pid

#检测pid
function getPid()
{
    echo "检测状态---------------------------------------------"
    pid=`ps -ef | grep -n ${PACKAGE_NAME} | grep -v grep | awk '{print $2}'`
    if [ ${pid} ]
    then
        echo "运行pid：${pid}"
    else
        echo "未运行"
    fi
}

#启动程序
function start()
{
    #启动前，先停止之前的
    stop
    if [ ${pid} ]
    then
        echo "停止程序失败，无法启动"
    else
        echo "启动程序---------------------------------------------"

        #选择语言类型
        read -p "输入程序类型(java,javac)，下一步按回车键(默认：${languageType})：" read_languageType
        if [ ${read_languageType} ]
        then
            languageType=${read_languageType}
        fi
        echo "选择程序类型：${languageType}"

        #进入运行包目录
        cd ${BASE_DIR_PATH}

        #分类启动
        if [ "${languageType}" == "javac" ]
        then
            if [ ${MAIN_CLASS} ]
            then
                nohup java -cp conf:lib\*.jar:${PACKAGE_NAME}.jar ${MAIN_CLASS} >${BASE_DIR_PATH}/${PACKAGE_NAME}.out 2>&1 &
               #nohup java -cp conf:lib\*.jar:${PACKAGE_NAME}.jar ${MAIN_CLASS} >/dev/null 2>&1 &
            fi
        elif [ "${languageType}" == "java" ]
        then
            echo ${BASE_DIR_PATH}/${PACKAGE_NAME}.jar
            nohup java -jar ${BASE_DIR_PATH}/${PACKAGE_NAME}.jar >${BASE_DIR_PATH}/${PACKAGE_NAME}.out 2>&1 &
            # java -jar ${BASE_DIR_PATH}/${BASE_ZIP_NAME}/${PACKAGE_NAME}.jar
        fi

        #查询是否有启动进程
        getPid
        if [ ${pid} ]
        then
            echo "已启动"
            #nohup日志
            tail -n 50 -f ${BASE_DIR_PATH}/${PACKAGE_NAME}.out
        else
            echo "启动失败"
        fi
    fi
}

#停止程序
function stop()
{
    getPid
    if [ ${pid} ]
    then
        echo "停止程序---------------------------------------------"
        kill -9 ${pid}

        getPid
        if [ ${pid} ]
        then
            #stop
            echo "停止失败"
        else
            echo "停止成功"
        fi
    fi
}

#启动时带参数，根据参数执行
if [ ${#} -ge 1 ]
then
    case ${1} in
        "start")
            start
        ;;
        "restart")
            start
        ;;
        "stop")
            stop
        ;;
        *)
            echo "${1}无任何操作"
        ;;
    esac
else
    echo "
    command如下命令：
    start：启动
    stop：停止进程
    restart：重启
    示例命令如：./genshin-helper.sh start
    "
fi
