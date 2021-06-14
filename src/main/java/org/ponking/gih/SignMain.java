package org.ponking.gih;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ponking.gih.gs.DailyTask;
import org.ponking.gih.gs.GenshinHelperProperties;
import org.ponking.gih.push.MessagePush;
import org.ponking.gih.util.FileUtils;

import java.io.File;
import java.net.URISyntaxException;

/**
 * @Author ponking
 * @Date 2021/5/7 10:09
 */
public class SignMain {

    private static Logger logger = LogManager.getLogger(SignMain.class.getName());

    /**
     * 1. args[0]:cookie args[1]:stuid args[2]:stoken
     * 2. args[0]:cookie args[1]:stuid args[2]:stoken args[3]:scKey
     * 3. cookie stuid stoken corpid corpsecret agentid
     *
     * @param args
     * @throws URISyntaxException
     */
    public static void main(String[] args) throws Exception {
        MessagePush messagePush = null;
        boolean pushed = false;
        String isGenUsers = System.getProperty("ponking.gen.users");
        GenshinHelperProperties properties = null;
        logger.info("是否需生成完整配置：{}", isGenUsers != null ? (isGenUsers.equals("true") ? true : false) : false);
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
        if (properties != null) {
            for (GenshinHelperProperties.Account account : properties.getAccount()) {
                DailyTask dailyTask = new DailyTask(properties.getMode(), properties.getSckey(), properties.getCorpid(),
                        properties.getCorpsecret(), properties.getAgentid(), account);
                dailyTask.doDailyTask();
                if (dailyTask.getMessagePush() != null && messagePush == null) { // 初始化日志任务
                    messagePush = dailyTask.getMessagePush();
                    pushed = dailyTask.isPushed();
                }
            }
        }
        if (pushed) {
            messagePush.sendMessage("原神签到日志", FileUtils.loadDailyFile());
        }
    }
}
