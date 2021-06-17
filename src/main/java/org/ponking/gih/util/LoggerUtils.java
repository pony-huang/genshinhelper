package org.ponking.gih.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @Author ponking
 * @Date 2021/6/16 20:06
 */
public class LoggerUtils {

    private static final Logger logger = LogManager.getLogger(LoggerUtils.class.getName());

    public static boolean flag = true;

    private static StringBuffer log;

    static {
        if (System.getProperty("gslog") != null) {
            flag = Boolean.parseBoolean(System.getProperty("gslog"));
            log = new StringBuffer();
        }
    }

    public static void warn(String message, Object... params) {
        if (flag) {
            logger.warn(message, params);
        } else {
            log.append(getMessage(message, params) + "\n");
        }
    }


    public static void info(String message, Object... params) {
        if (flag) {
            logger.info(message, params);
        } else {
            log.append(getMessage(message, params) + "\n");
        }
    }


    public static void debug(String message, Object... params) {
        if (flag) {
            logger.info(message, params);
        } else {
            log.append(getMessage(message, params) + "\n");
        }
    }

    private static String getMessage(String message, Object... params) {
        return String.format(message.replace("{}", "%s"), params);
    }

    public static String getLog() {
        return log.toString();
    }
}
