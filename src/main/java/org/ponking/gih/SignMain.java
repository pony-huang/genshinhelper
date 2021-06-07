package org.ponking.gih;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ponking.gih.gs.GenshinHelperProperties;
import org.ponking.gih.gs.Task;
import org.ponking.gih.push.MessagePush;
import org.ponking.gih.util.GetstokenUtils;
import org.ponking.gih.util.LoadLogFileResource;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
        logger.info("isGenUsers：{}", isGenUsers);
        if ("true".equals(isGenUsers) && args.length == 1) {
            GetstokenUtils.gen(args[0]);
            logger.info("生成用户cookie成功！！！，文件名称：{}", "genshin-users.yaml");
            return;
        }
        if (args.length == 1) {
            String baseDir = "";
            if ("genshin-helper.yaml".equals(args[0])) {
                baseDir = System.getProperty("user.dir");
            }
            String fileName = baseDir + File.separator + args[0];
            logger.info("配置文件路径：{}", fileName);
            File file = new File(fileName);
            if (!file.exists()) {
                throw new FileNotFoundException("配置文件不存在：" + fileName);
            }
            InputStream is = new FileInputStream(file);
            Yaml yaml = new Yaml(new Constructor(GenshinHelperProperties.class));
            GenshinHelperProperties properties = yaml.load(is);

            for (GenshinHelperProperties.Account account : properties.getAccount()) {
                Task task = new Task(properties.getMode(), properties.getSckey(), properties.getCorpid(),
                        properties.getCorpsecret(), properties.getAgentid(), account);
                task.doDailyTask();
                if (task.getMessagePush() != null && messagePush == null) { // 初始化日志任务
                    messagePush = task.getMessagePush();
                    pushed = task.isPushed();
                }
            }
        } else {
            Task task = new Task(args);
            task.doDailyTask();
            if (task.getMessagePush() != null) {
                messagePush = task.getMessagePush();
                pushed = task.isPushed();
            }
        }
        if (pushed) {
            messagePush.sendMessage("原神签到日志", LoadLogFileResource.loadDailyFile());
        }
    }
}
