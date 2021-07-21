package org.ponking.gih.util;

/**
 * @Author ponking
 * @Date 2021/7/21 20:54
 */
public interface Logger {

    void info(String message, Object... params);

    void warn(String message, Object... params);

    void error(String message, Object... params);


    void debug(String message, Object... params);

    String getCurThreadLog() throws UnsupportedOperationException;
}
