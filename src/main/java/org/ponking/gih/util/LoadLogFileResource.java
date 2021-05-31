package org.ponking.gih.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @Author ponking
 * @Date 2021/5/7 13:13
 */
public class LoadLogFileResource {

    private LoadLogFileResource() {
    }

    public static String loadDailyFile() {
        String path = System.getProperties().get("user.dir") + "/logs/daily.log";
        FileInputStream fis = null;
        String log = "";
        try {
            fis = new FileInputStream(path);
            int size = fis.available();
            byte[] cache = new byte[size];
            fis.read(cache);
            log = new String(cache, StandardCharsets.UTF_8);
            return log;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return log;
    }
}
