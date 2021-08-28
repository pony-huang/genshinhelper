package org.ponking.gih;

import com.alibaba.fastjson.JSON;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ponking.gih.sign.Constant;
import org.ponking.gih.sign.DailyTask;
import org.ponking.gih.sign.gs.GenshinHelperProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * @Author ponking
 * @Date 2021/5/7 10:09
 */
public class SignMain {

    public static Logger log = LogManager.getLogger(SignMain.class.getName());


    public static void main(String[] args) throws Exception {
        mainHandler(new KeyValueClass());
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
        CountDownLatch countDownLatch = new CountDownLatch(tasks.size());
        for (int i = 0; i < tasks.size(); i++) {
            final int index = i;
            tasks.get(i).setWorkDir(Constant.GENSHIN_TENCENT_LOGS_PATH);
            new Thread(() -> {
                tasks.get(index).doDailyTask(countDownLatch);
            }, Constant.GENSHIN_THREAD_PREFIX + i).start();
        }
        countDownLatch.await();
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


    //    /**
//     * 简单执行，可配合Linux cron
//     *
//     * @throws Exception
//     */
//    public static void simpleMainHandler(String[] args) throws Exception {
//        GenshinHelperProperties properties = loadProperties(args);
//        List<DailyTask> tasks = createDailyTasks(properties);
//        ThreadPoolExecutor executor =
//                new ThreadPoolExecutor(3, 10, 60,
//                        TimeUnit.SECONDS,
//                        new LinkedBlockingDeque<>(10),
//                        new GenTaskThreadFactory(),
//                        new ThreadPoolExecutor.AbortPolicy());
//        for (DailyTask task : tasks) {
//            executor.execute(task);
//        }
//        executor.shutdown();
//    }
}
