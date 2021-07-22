package org.ponking.gih.util;

/**
 * @Author ponking
 * @Date 2021/7/21 23:10
 */
public class AbstractLogger implements Logger {

    @Override
    public void info(String message, Object... params) {

    }

    @Override
    public void warn(String message, Object... params) {

    }

    @Override
    public void error(String message, Object... params) {

    }

    @Override
    public void debug(String message, Object... params) {

    }

    @Override
    public String getCurThreadLog() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
