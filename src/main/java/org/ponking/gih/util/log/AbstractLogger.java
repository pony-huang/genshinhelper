package org.ponking.gih.util.log;

import org.apache.logging.log4j.LogManager;

/**
 * @Author ponking
 * @Date 2021/7/21 23:10
 */
public class AbstractLogger implements Logger {

    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(LoggerDefault.class.getName());


    @Override
    public void warn(String message, Object... params) {
        logger.warn(message, params);
    }

    @Override
    public void error(String message, Object... params) {
        logger.error(message, params);
    }

    @Override
    public void info(String message, Object... params) {
        logger.info(message, params);
    }

    @Override
    public void debug(String message, Object... params) {
        logger.debug(message, params);
    }

    @Override
    public String getCurThreadLog() throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}
