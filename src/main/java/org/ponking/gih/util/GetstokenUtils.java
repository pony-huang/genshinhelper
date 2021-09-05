package org.ponking.gih.util;

import com.alibaba.fastjson.JSONObject;
import org.ponking.gih.sign.gs.MiHoYoAbstractSign;
import org.ponking.gih.sign.gs.MiHoYoConfig;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @Author ponking
 * @Date 2021/5/31 15:34
 */
public class GetstokenUtils {

    static String cookie = "";

    private GetstokenUtils() {
    }


    public static void main(String[] args) {
        System.out.println(doGen(cookie));
    }


    public static Map<String, Object> doGen(String cookie) {
        Map<String, Object> user = new HashMap<>();
        Map<String, String> headers = getCookieHeader(cookie);
        String url = String.format(MiHoYoConfig.HUB_COOKIE2_URL, headers.get("login_ticket"), headers.get("login_uid"));
        MiHoYoAbstractSign helperHeader = new MiHoYoAbstractSign(cookie) {
            @Override
            public void doSign() throws Exception {

            }
        };
        JSONObject result = HttpUtils.
                doGet(url, helperHeader.getHeaders());
        System.out.println(result);
        if (!"OK".equals(result.get("message"))) {
            System.out.println("login_ticket已失效,请重新登录获取");
        } else {
            String stoken = (String) result.getJSONObject("data").getJSONArray("list").getJSONObject(0).get("token");
            String stuid = headers.get("login_uid");
            user.put("stoken", stoken);
            user.put("stuid", stuid);
        }
        return user;
    }


    @Deprecated
    public static void gen(String fileName) {
        String baseDir = "";
        FileOutputStream fos = null;
        FileInputStream fis = null;
        try {
            if ("genshin-gen.properties".equals(fileName)) {
                baseDir = System.getProperty("user.dir");
            }
            fileName = baseDir + File.separator + fileName;
            File file = new File(fileName);
            if (!file.exists()) {
                throw new FileNotFoundException("配置文件不存在：" + fileName);
            }
            fis = new FileInputStream(file);
            Properties pro = new Properties();
            pro.load(fis);
            Map<String, Object> data = new HashMap<>();
            for (Object key : pro.keySet()) {
                String cookie = (String) pro.get(key);
                Map<String, Object> user = doGen(cookie);
                data.put((String) key, user);
            }
            Yaml yaml = new Yaml();
            StringWriter writer = new StringWriter();
            yaml.dump(data, writer);
            File outFile = new File("genshin-users.yaml");
            if (outFile.exists()) {
                outFile.delete();
            }
            fos = new FileOutputStream(outFile);
            fos.write(writer.toString().getBytes());

            fis.close();
            fos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Map<String, String> getCookieHeader(String cookie) {
        String[] split = cookie.split(";");
        Map<String, String> map = new HashMap<>();
        for (String s : split) {
            String h = s.trim();
            String[] item = h.split("=");
            map.put(item[0], item[1]);
        }
        return map;
    }

}
