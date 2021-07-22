package org.ponking.gih.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @Author ponking
 * @Date 2021/6/16 20:06
 */
public class LoggerDefault implements org.ponking.gih.util.Logger {

    private static final Logger logger = LogManager.getLogger(LoggerDefault.class.getName());


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
