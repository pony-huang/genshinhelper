package org.ponking.gih;

import com.alibaba.fastjson.JSON;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.ponking.gih.sign.Constant;
import org.ponking.gih.sign.DailyTask;
import org.ponking.gih.sign.GenTaskThreadFactory;
import org.ponking.gih.sign.gs.GenshinHelperProperties;
import org.ponking.gih.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
        try {
            loadLog4j2();
            log = LogManager.getLogger(SignMain.class.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        simpleMainHandler(args);
    }

    /**
     * 简单执行，可配合Linux cron
     *
     * @throws Exception
     */
    public static void simpleMainHandler(String[] args) throws Exception {
        GenshinHelperProperties properties = loadProperties(args);
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
     * 简单执行，可配合Linux cron
     *
     * @throws Exception
     */
    public static void simpleMainHandlerByThread(String[] args) throws Exception {
        GenshinHelperProperties properties = loadProperties(args);
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

    public static GenshinHelperProperties loadProperties(String[] args) throws FileNotFoundException {
        String conf = System.getProperty("genshin.config");
        if (isWindows()) {
            String baseDir = System.getProperty("user.dir");
            return FileUtils.loadConfig(Objects.isNull(conf) ? baseDir + File.separator + "conf" + File.separator + "config.yaml" : conf);
        }
        return FileUtils.loadConfig(conf);
    }

    public static void loadLog4j2() throws IOException {
        String gsLog = System.getProperty("genshin.logger");
        if (isWindows()) {
            String path = System.getProperty("user.dir") + File.separator + "conf"
                    + File.separator + "log4j2.xml";
            URL url = SignMain.class.getResource(gsLog == null ? path : gsLog);
            if (url == null) {
                return;
            }
            ConfigurationSource source = new ConfigurationSource(url.openStream(), url);
            Configurator.initialize(null, source);
            return;
        }
        File file = new File(gsLog);
        if (!file.exists()) {
            throw new RuntimeException("genshin.logger参数有误,gsLog=" + gsLog);
        }
        ConfigurationSource source = new ConfigurationSource(new FileInputStream(file), file.toURL());
        Configurator.initialize(null, source);
    }

    /**
     * 用于本地测试，笔者使用系统为window
     *
     * @return
     */
    private static boolean isWindows() {
        String osName = System.getProperty("os.name");
        return osName.toLowerCase().contains("windows");
    }
}
