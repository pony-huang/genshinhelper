package org.ponking.gih.gs;

import org.ponking.gih.push.MessagePush;
import org.ponking.gih.util.LoadLogFileResource;

/**
 * 主要用于多账号
 *
 * @Author ponking
 * @Date 2021/6/1 16:21
 */
@Deprecated
public class TaskLog {


    private MessagePush messagePush;

    private boolean pushed;

    public TaskLog(MessagePush messagePush, boolean pushed) {
        this.messagePush = messagePush;
        this.pushed = pushed;
    }

    public void printLog() {
        if (pushed) {
            String log = LoadLogFileResource.loadDailyFile();
            int start = 0;
            while (log.length() > start) {
                String ms = log.substring(start, Math.min(start + 512, log.length()));
                messagePush.sendMessage(start == 0 ? "原神签到日志" : "", ms);
                start += 512;
            }
        }
    }
}
