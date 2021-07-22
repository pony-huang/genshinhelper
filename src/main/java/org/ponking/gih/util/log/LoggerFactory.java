package org.ponking.gih.util.log;

/**
 * @Author ponking
 * @Date 2021/7/21 20:54
 */
public class LoggerFactory {


    private static boolean flag = true;

    private static Logger instance = null;

    private LoggerFactory() {
    }

    static {
        if (System.getProperty("gslog") != null) {
            flag = Boolean.parseBoolean(System.getProperty("gslog"));
        }
    }

    public static Logger getInstance() {
        if (instance == null) {
            synchronized (LoggerFactory.class) {
                if (instance == null) {
                    if (!flag) {
                        instance = new LoggerCache();
                    } else {
                        instance = new LoggerDefault();
                    }
                }
            }
        }
        return instance;
    }

    public static boolean isFlag() {
        return flag;
    }
}
