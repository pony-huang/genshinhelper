package org.ponking.gih;

import com.alibaba.fastjson.JSON;
import org.ponking.gih.push.MessagePush;
import org.ponking.gih.push.ServerChanMessagePush;
import org.ponking.gih.push.WeixinCPMessagePush;
import org.ponking.gih.server.weixincp.service.WXUserInfo;
import org.ponking.gih.sign.DailyTask;
import org.ponking.gih.sign.gs.GenshinHelperProperties;
import org.ponking.gih.util.FileUtils;
import org.ponking.gih.util.log.LoggerFactory;

import java.io.File;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author ponking
 * @Date 2021/5/7 10:09
 */
public class SignMain {

    private static ThreadPoolExecutor executor =
            new ThreadPoolExecutor(3, 10, 60,
                    TimeUnit.SECONDS,
                    new LinkedBlockingDeque<>(10), new ThreadPoolExecutor.AbortPolicy());


    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String isGenUsers = System.getProperty("genshin.gen.users");
        GenshinHelperProperties properties = null;
        LoggerFactory.getInstance().info("是否需生成完整配置：{}", isGenUsers != null ? (isGenUsers.equals("true") ? true : false) : false);
        if ("true".equals(isGenUsers) && args.length == 1) {
            // 配置文件不完整，需要生成完整配置文件
            FileUtils.outPutSettingYaml(args[0]);
            properties = FileUtils.loadSettingYaml(System.getProperty("user.dir") + File.separator + "genshin-helper-auto.yaml");
        } else if (args.length == 1) {
            // 配置文件完整，直接读取
            properties = FileUtils.loadSettingYaml(args[0]);
        } else {
            throw new UnsupportedOperationException("参数异常");
        }
        createUserTaskAndDo(properties);
    }


    /**
     * 腾讯云函数
     *
     * @throws Exception
     */
    public static void mainHandler(KeyValueClass keyValueClass) throws Exception {
        String config = System.getProperty("config");
        if (null == config) {
            LoggerFactory.getInstance().info("config配置为空！");
            return;
        }
        GenshinHelperProperties properties = JSON.parseObject(config, GenshinHelperProperties.class);
        createUserTaskAndDo(properties);
    }

    private static void createUserTaskAndDo(GenshinHelperProperties properties) throws Exception {
        if (properties != null) {
            for (GenshinHelperProperties.Account account : properties.getAccount()) {
                DailyTask dailyTask = new DailyTask(properties.getMode(), properties.getSckey(), properties.getCorpid(),
                        properties.getCorpsecret(), properties.getAgentid(), account);
                executor.execute(dailyTask);
            }
        }
        executor.shutdown();
        if (LoggerFactory.isFlag()) {
            MessagePush messagePush = null;
            if ("serverChan".equals(properties.getMode()) && properties.getSckey() != null) {
                messagePush = new ServerChanMessagePush(properties.getSckey());
            } else if ("weixincp".equals(properties.getMode())) {
                WXUserInfo info = new WXUserInfo(properties.getCorpid(), properties.getCorpsecret(), properties.getAgentid());
                messagePush = new WeixinCPMessagePush(info);
            } else {
                throw new UnsupportedOperationException("参数异常");
            }
            messagePush
                    .sendMessage("原神签到日志", FileUtils.loadDaily());
        }

    }
}
