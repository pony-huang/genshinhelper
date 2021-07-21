package org.ponking.gih.util;

/**
 * @Author ponking
 * @Date 2021/7/21 20:55
 */
public class LoggerCache implements Logger {


    private final ThreadLocal<StringBuilder> localCache = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder();
        }
    };

    @Override
    public void info(String message, Object... params) {
        StringBuilder data = localCache.get();
        data.append(getMessage(message, params)).append("\n");
    }

    @Override
    public void warn(String message, Object... params) {
        StringBuilder data = localCache.get();
        data.append(getMessage(message, params)).append("\n");
    }

    @Override
    public void error(String message, Object... params) {
        StringBuilder data = localCache.get();
        data.append(getMessage(message, params)).append("\n");
    }

    @Override
    public void debug(String message, Object... params) {
        StringBuilder data = localCache.get();
        data.append(getMessage(message, params)).append("\n");
    }

    private String getMessage(String message, Object... params) {
        return String.format(message.replace("{}", "%s"), params);
    }

    @Override
    public String getCurThreadLog() {
        return localCache.get().toString();
    }
}
