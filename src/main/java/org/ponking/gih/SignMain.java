package org.ponking.gih;

import com.alibaba.fastjson.JSON;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ponking.gih.sign.DailyTask;
import org.ponking.gih.sign.GenTaskThreadFactory;
import org.ponking.gih.sign.gs.GenshinHelperProperties;
import org.ponking.gih.util.FileUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Author ponking
 * @Date 2021/5/7 10:09
 */
public class SignMain {

    private static Logger log = LogManager.getLogger(SignMain.class.getName());

    public static void main(String[] args) throws Exception {
        simpleMainHandler(args);
    }

    public static GenshinHelperProperties properties(String[] args) throws FileNotFoundException {
        String baseDir = System.getProperty("user.dir");
        return FileUtils.loadConfig(baseDir + File.separator + "conf" + File.separator + "config.yaml");
    }


    /**
     * 简单执行，可配合Linux cron
     *
     * @throws Exception
     */
    public static void simpleMainHandler(String[] args) throws Exception {
        String baseDir = System.getProperty("user.dir");
        GenshinHelperProperties properties = FileUtils.loadConfig(baseDir + File.separator + "conf" + File.separator + "config.yaml");
        List<DailyTask> tasks = createDailyTasks(properties);
        ThreadPoolExecutor executor =
                new ThreadPoolExecutor(3, 10, 60,
                        TimeUnit.SECONDS,
                        new LinkedBlockingDeque<>(10),
                        new GenTaskThreadFactory(),
                        new ThreadPoolExecutor.AbortPolicy());
        for (DailyTask task : tasks) {
            executor.execute(task);
        }
        executor.shutdown();
    }

    /**
     * 腾讯云函数
     *
     * @throws Exception
     */
    public static void mainHandler(KeyValueClass keyValueClass) throws Exception {
        String config = System.getProperty("config");
        if (null == config) {
            log.info("config配置为空!!!");
            return;
        }
        GenshinHelperProperties properties = JSON.parseObject(config, GenshinHelperProperties.class);
        List<DailyTask> tasks = createDailyTasks(properties);
        ThreadPoolExecutor executor =
                new ThreadPoolExecutor(3, 10, 60,
                        TimeUnit.SECONDS,
                        new LinkedBlockingDeque<>(10),
                        new GenTaskThreadFactory(),
                        new ThreadPoolExecutor.AbortPolicy());
        for (DailyTask task : tasks) {
            task.setWorkDir("/tmp/logs");
            executor.execute(task);
        }
        executor.shutdown();
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
                DailyTask dailyTask = new DailyTask(properties.getMode(), properties.getSckey(), properties.getCorpid(),
                        properties.getCorpsecret(), properties.getAgentid(), account);
                tasks.add(dailyTask);
            }
        }
        return tasks;
    }

}
