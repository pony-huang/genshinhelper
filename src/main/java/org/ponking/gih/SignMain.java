package org.ponking.gih;

import com.alibaba.fastjson.JSON;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ponking.gih.sign.Constant;
import org.ponking.gih.sign.DailyTask;
import org.ponking.gih.sign.GenTaskThreadFactory;
import org.ponking.gih.sign.gs.GenshinHelperProperties;
import org.ponking.gih.util.FileUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @Author ponking
 * @Date 2021/5/7 10:09
 */
public class SignMain {

    public static Logger log = null;


    static {
        // 默认执行环境腾讯云函数,用于日志配置，log4j2
        System.setProperty(Constant.GENSHIN_ENV_LOG_PATH, Constant.ENV_TENCENT_LOG_PATH);
        System.setProperty(Constant.GENSHIN_EXEC, "腾讯云函数");
        log = LogManager.getLogger(SignMain.class.getName());
    }


    public static void main(String[] args) throws Exception {
        System.setProperty(Constant.GENSHIN_ENV_LOG_PATH, Constant.ENV_DEFAULT_LOG_PATH);
        System.setProperty(Constant.GENSHIN_EXEC, System.getProperty("os.name"));
        String config = getConfig(Constant.ENV_DEFAULT_CONFIG_PATH);
        GenshinHelperProperties properties = FileUtils.loadConfig(config);
        exec(properties);
    }

    /**
     * 腾讯云函数
     *
     * @throws Exception
     */
    public static void mainHandler(KeyValueClass keyValueClass) throws Exception {
        String config = getConfig(Constant.ENV_TENCENT_CONFIG_PATH);
        GenshinHelperProperties properties = JSON.parseObject(config, GenshinHelperProperties.class);
        exec(properties);
    }

    public static void exec(GenshinHelperProperties properties) throws InterruptedException {
        List<DailyTask> tasks = createDailyTasks(properties);
        ThreadPoolExecutor executor =
                new ThreadPoolExecutor(3, 5, 60,
                        TimeUnit.SECONDS,
                        new LinkedBlockingDeque<>(10),
                        new GenTaskThreadFactory(),
                        new ThreadPoolExecutor.AbortPolicy());
        for (DailyTask task : tasks) {
            executor.execute(task);
        }
        executor.shutdown();
        while (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            log.info("GenTaskThreadPool is not closed yet!");
        }
        log.info("GenTaskThreadPool shutdown");
    }

    @Deprecated
    private static void execDownLatch(GenshinHelperProperties properties) throws InterruptedException {
        List<DailyTask> tasks = createDailyTasks(properties);
        CountDownLatch countDownLatch = new CountDownLatch(tasks.size());
        for (int i = 0; i < tasks.size(); i++) {
            final int index = i;
            new Thread(() -> {
                tasks.get(index).doDailyTask(countDownLatch);
            }, Constant.GENSHIN_THREAD_PREFIX + i).start();
        }
        countDownLatch.await();
    }

    private static String getConfig(String path) {
        String config = System.getProperty(path);
        if (null == config) {
            throw new RuntimeException("config配置为空!!!");
        }
        return config;
    }

    /**
     * 创建任务
     *
     * @param properties
     * @return
     */
    public static List<DailyTask> createDailyTasks(GenshinHelperProperties properties) {
        List<DailyTask> tasks = new ArrayList<>();
        if (properties != null) {
            for (GenshinHelperProperties.Account account : properties.getAccount()) {
                DailyTask task = DailyTask.builder()
                        .account(account.getCookie())
                        .miHoYoSign(account)
                        .signMode(properties.getSignMode())
                        .msgPush(properties.getMode(),
                                properties.getSckey(),
                                properties.getCorpid(),
                                properties.getCorpsecret(),
                                properties.getAgentid(),
                                account.getToUser())
                        .build();
                tasks.add(task);
            }
        }
        return tasks;
    }

}
