package org.ponking.gih;

import com.alibaba.fastjson.JSON;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ponking.gih.gs.DailyTask;
import org.ponking.gih.gs.GenshinHelperProperties;
import org.ponking.gih.push.MessagePush;
import org.ponking.gih.util.FileUtils;
import org.ponking.gih.util.LoggerUtils;

import java.io.File;

/**
 * @Author ponking
 * @Date 2021/5/7 10:09
 */
public class SignMain {

    private static Logger logger = LogManager.getLogger(SignMain.class.getName());

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        MessagePush messagePush = null;
        boolean pushed = false;
        String isGenUsers = System.getProperty("ponking.gen.users");
        GenshinHelperProperties properties = null;
        LoggerUtils.info("是否需生成完整配置：{}", isGenUsers != null ? (isGenUsers.equals("true") ? true : false) : false);
        if ("true".equals(isGenUsers) && args.length == 1) {
            // 配置文件不完整，需要生成完整配置文件
            FileUtils.outPutSettingYaml(args[0]);
            properties = FileUtils.loadSettingYaml(System.getProperty("user.dir") + File.separator + "genshin-helper-auto.yaml");
        } else if (args.length == 1) {
            // 配置文件完整，直接读取
            properties = FileUtils.loadSettingYaml(args[0]);
        } else {
            DailyTask dailyTask = new DailyTask(args);
            dailyTask.doDailyTask();
            if (dailyTask.getMessagePush() != null) {
                messagePush = dailyTask.getMessagePush();
                pushed = dailyTask.isPushed();
            }
        }
        createUserTaskAndDo(messagePush, pushed, properties);
    }


    /**
     * 腾讯云函数
     *
     * @param keyValueClass
     * @throws Exception
     */
    public static void mainHandler(KeyValueClass keyValueClass) throws Exception {
        MessagePush messagePush = null;
        boolean pushed = false;
        String config = System.getProperty("config");
        if (null == config) {
            LoggerUtils.info("config配置为空！");
            return;
        }
        GenshinHelperProperties properties = JSON.parseObject(config, GenshinHelperProperties.class);
        createUserTaskAndDo(messagePush, pushed, properties);
    }

    private static void createUserTaskAndDo(MessagePush messagePush, boolean pushed, GenshinHelperProperties properties) throws Exception {
        if (properties != null) {
            for (GenshinHelperProperties.Account account : properties.getAccount()) {
                DailyTask dailyTask = new DailyTask(properties.getMode(), properties.getSckey(), properties.getCorpid(),
                        properties.getCorpsecret(), properties.getAgentid(), account);
                dailyTask.doDailyTask();
                // 初始化日志任务
                if (dailyTask.getMessagePush() != null && messagePush == null) {
                    messagePush = dailyTask.getMessagePush();
                    pushed = dailyTask.isPushed();
                }
            }
        }
        if (pushed) {
            messagePush.sendMessage("原神签到日志", FileUtils.loadDaily());
        }
    }
}
