package org.ponking.gih;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ponking.gih.gs.Task;

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
        new Task(args).doDailyTask();
    }
}
