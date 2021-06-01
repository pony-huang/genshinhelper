package org.ponking.gih.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;

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
            log = new String(cache);
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

    public static void clearDailyFile() {
        String path = System.getProperties().get("user.dir") + "/logs/daily.log";
        File file = new File(path);
        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
