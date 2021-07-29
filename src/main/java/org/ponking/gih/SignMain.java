package org.ponking.gih;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.ponking.gih.sign.DailyTask;
import org.ponking.gih.sign.SignJob;
import org.ponking.gih.sign.gs.GenshinHelperProperties;
import org.ponking.gih.util.FileUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

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
@Slf4j
public class SignMain {


    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        quartzMainHandler(args);

    }

    public static void quartzMainHandler(String[] args) throws FileNotFoundException, SchedulerException {
        String baseDir = System.getProperty("user.dir");
        GenshinHelperProperties properties = FileUtils.loadConfig(baseDir + File.separator + "conf"+File.separator+"config.yaml");
        doTask(createDailyTasks(properties),properties.getCron());
    }

    public static GenshinHelperProperties properties(String[] args) throws FileNotFoundException, SchedulerException {
        String baseDir = System.getProperty("user.dir");
        return FileUtils.loadConfig(baseDir + File.separator + "conf"+File.separator+"config.yaml");
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
                        new LinkedBlockingDeque<>(10), new ThreadPoolExecutor.AbortPolicy());
        for (DailyTask task : tasks) {
            executor.execute(task);
        }
        executor.shutdown();
    }

    public static List<DailyTask> createDailyTasks(GenshinHelperProperties properties){
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

    public static void doTask(List<DailyTask> tasks,String cron) throws SchedulerException {
        //创建一个scheduler
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        //创建一个Trigger
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("signTrigger", "signGroup")
                .withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();

        //创建一个job
        JobDetail job = JobBuilder.newJob(SignJob.class)
                .withIdentity("signJobDetail", "signGroup").build();
        job.getJobDataMap().put("tasks", tasks);

        //注册trigger并启动scheduler
        scheduler.scheduleJob(job,trigger);
        scheduler.start();
    }
}
