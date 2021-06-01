package org.ponking.gih.gs;

import org.ponking.gih.push.MessagePush;
import org.ponking.gih.util.LoadLogFileResource;

/**
 * 主要用于多账号
 *
 * @Author ponking
 * @Date 2021/6/1 16:21
 */
public class TaskLog {


    private MessagePush messagePush = null;

    private boolean pushed;

    public TaskLog(MessagePush messagePush, boolean pushed) {
        this.messagePush = messagePush;
        this.pushed = pushed;
    }

    public void printLog() {
        if (pushed) {
            String log = LoadLogFileResource.loadDailyFile();
            String[] ms = log.split("-------分割线-------");
            for (String m : ms) {
                messagePush.sendMessage("原神签到日志", m);
            }
        }
    }
}
