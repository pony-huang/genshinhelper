package org.ponking.gih.sign;

import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * @Author HuangPengGuang
 * @Date 2021/7/29 11:39
 */
public class SignJob implements Job {


    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        ThreadPoolExecutor executor =
                new ThreadPoolExecutor(3, 10, 60,
                        TimeUnit.SECONDS,
                        new LinkedBlockingDeque<>(10),new GenTaskThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        List<DailyTask> tasks = (List<DailyTask>) dataMap.get("tasks");
        for (DailyTask task : tasks) {
            executor.execute(task);
        }
        executor.shutdown();
    }
}
