package org.ponking.gih;

import com.alibaba.fastjson.JSON;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ponking.gih.sign.Constant;
import org.ponking.gih.sign.DailyTask;
import org.ponking.gih.sign.GenTaskThreadFactory;
import org.ponking.gih.sign.MessageTask;
import org.ponking.gih.sign.gs.GenshinHelperProperties;
import org.ponking.gih.util.FileUtils;
import org.ponking.gih.util.MailUtil;
import org.ponking.gih.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;


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

    public static void exec(GenshinHelperProperties properties) throws InterruptedException, ExecutionException {
        List<DailyTask> tasks = createDailyTasks(properties);
        ThreadPoolExecutor executor =
                new ThreadPoolExecutor(3, 5, 60,
                        TimeUnit.SECONDS,
                        new LinkedBlockingDeque<>(10),
                        new GenTaskThreadFactory(),
                        new ThreadPoolExecutor.AbortPolicy());
        List<Future> futures = new ArrayList<>();
        for (DailyTask task : tasks) {
            futures.add(executor.submit((Callable<? extends Object>) task));
        }

        if (StringUtils.isNotEmpty(properties.getUsername()) &&
                StringUtils.isNotEmpty(properties.getPassword()) &&
                StringUtils.isNotEmpty(properties.getHostName()) &&
                Objects.nonNull(properties.getPort())) {
            MailUtil.init(properties.getUsername(), properties.getPassword(), properties.getPort(), properties.getHostName());
        }

        while (true) {
            boolean all = true;
            for (Future future : futures) {
                boolean done = future.isDone();
                all = all & done;
                if (done) {
                    Object obj = future.get();
                    if (Objects.nonNull(obj)) {
                        MessageTask messageTask = (MessageTask) obj;
                        if (Objects.nonNull(messageTask.getMessagePush()) && !messageTask.isPush()) {
                            String fileName = messageTask.getFileName();
                            messageTask.getMessagePush().sendMessage("原神签到", FileUtils.loadDaily(FileUtils.LOG_FILE_PATH + File.separator + fileName));
                            messageTask.setPush(true);
                        }
                    }
                } else {
                    TimeUnit.SECONDS.sleep(2L);
                }
            }

            if (all) {
                break;
            }
        }
        executor.shutdown();
        while (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
            log.info("GenTaskThreadPool is not closed yet!");
        }
        log.info("GenTaskThreadPool shutdown");
        System.exit(0);
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
                DailyTask.DailyTaskBuilder dailyTaskBuilder = DailyTask.builder()
                        .account(account.getCookie())
                        .miHoYoSign(account)
                        .signMode(properties.getSignMode());
                String priorityMode = StringUtils.isNotEmpty(account.getPushType()) ? account.getPushType() : properties.getMode();
                buildPush(priorityMode, dailyTaskBuilder, properties, account);
                DailyTask task = dailyTaskBuilder.build();
                tasks.add(task);
            }
        }
        return tasks;
    }

    public static void buildPush(String priorityMode,
                                 DailyTask.DailyTaskBuilder dailyTaskBuilder,
                                 GenshinHelperProperties properties,
                                 GenshinHelperProperties.Account account) {
        switch (priorityMode) {
            case Constant.MODE_SERVER_CHAN: {
                if (StringUtils.isBank(properties.getSckey())) {
                    throw new RuntimeException("参数有误,scKey尚未配置");
                }
                dailyTaskBuilder.serverChanPush(properties.getSckey());
                break;
            }
            case Constant.MODE_SERVER_TURBO_CHAN: {
                if (StringUtils.isBank(properties.getSckey())) {
                    throw new RuntimeException("参数有误,scKey尚未配置");
                }
                dailyTaskBuilder.serverTurboChanPush(properties.getSckey());
                break;
            }
            case Constant.MODE_WEIXIN_CP_CHAN: {
                if (StringUtils.isBank(properties.getCorpid()) || StringUtils.isBank(properties.getCorpsecret()) || StringUtils.isBank(properties.getAgentid())) {
                    throw new RuntimeException("参数有误,scKey尚未配置");
                }
                dailyTaskBuilder.weiXinPush(properties.getCorpid(), properties.getCorpsecret(), properties.getAgentid(), account.getToUser());
                break;
            }
            case Constant.MODE_EMAIL_CHAN: {
                if (StringUtils.isBank(account.getEmail())) {
                    throw new RuntimeException("参数有误,email尚未配置");
                }
                dailyTaskBuilder.mailPush(properties.getUsername(), account.getEmail());
                break;
            }
            default:
                break;
        }
    }

}
