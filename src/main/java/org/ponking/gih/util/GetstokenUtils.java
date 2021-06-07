package org.ponking.gih.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.ponking.gih.gs.MiHoYoConfig;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.*;

/**
 * @Author ponking
 * @Date 2021/5/31 15:34
 */
public class GetstokenUtils {

    private GetstokenUtils() {
    }


    public static void main(String[] args) {
        doGen("{你的cookie}");
    }


    private static Map<String, Object> doGen(String cookie) {
        Map<String, Object> user = new HashMap<>();
        JSONObject result = HttpUtils.
                doGet(String.format(MiHoYoConfig.HUB_COOKIE2_URL, getCookieByName(cookie, "login_ticket"), getCookieByName(cookie, "account_id")), getHeaders(cookie));
        if (!"OK".equals(result.get("message"))) {
            System.out.println("login_ticket已失效,请重新登录获取");
        } else {
            String stoken = (String) result.getJSONObject("data").getJSONArray("list").getJSONObject(0).get("token");
            String stuid = getCookieByName(cookie, "account_id");
            System.out.println("stoken=" + stoken);
            System.out.println("stuid=" + stuid);
            user.put("stoken", stoken);
            user.put("stuid", stuid);
        }
        return user;
    }


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

    public static String getCookieByName(String cookie, String name) {
        String[] split = cookie.split(";");
        for (String s : split) {
            String h = s.trim();
            if (h.startsWith(name)) {
                return h.substring(h.indexOf('=') + 1);
            }
        }
        return null;
    }


    public static Header[] getHeaders(String cookie) {
        BasicHeader h1 = new BasicHeader("Cookie", cookie);
        BasicHeader h2 = new BasicHeader("User-Agent", MiHoYoConfig.USER_AGENT);
        BasicHeader h3 = new BasicHeader("Referer", MiHoYoConfig.REFERER_URL);
        BasicHeader h4 = new BasicHeader("Accept-Encoding", "gzip, deflate, br");
        BasicHeader h5 = new BasicHeader("x-rpc-channel", "appstore");
        BasicHeader h6 = new BasicHeader("accept-language", "zh-CN,zh;q=0.9,ja-JP;q=0.8,ja;q=0.7,en-US;q=0.6,en;q=0.5");
        BasicHeader h7 = new BasicHeader("accept-encoding", "gzip, deflate");
        BasicHeader h8 = new BasicHeader("x-requested-with", "com.mihoyo.hyperion");
        BasicHeader h9 = new BasicHeader("Host", "api-takumi.mihoyo.com");
        BasicHeader h10 = new BasicHeader("x-rpc-device_id", UUID.randomUUID().toString().toUpperCase());
        BasicHeader h11 = new BasicHeader("Content-Type", "application/json;charset=UTF-8");
        BasicHeader h12 = new BasicHeader("x-rpc-client_type", MiHoYoConfig.CLIENT_TYPE);
        BasicHeader h13 = new BasicHeader("x-rpc-app_version", MiHoYoConfig.APP_VERSION);
        BasicHeader h14 = new BasicHeader("DS", getDS());
        return new ArrayList<>(Arrays.asList(h1, h2, h3, h4, h5, h6, h7, h8, h9, h10, h11, h12, h13, h14)).toArray(new Header[0]);
    }


    public static String getDS() {
        String n = "h8w582wxwgqvahcdkpvdhbh2w9casgfl";
        String i = (System.currentTimeMillis() / 1000) + "";
        String r = getRandomStr();
        String c = DigestUtils.md5Hex("salt=" + n + "&t=" + i + "&r=" + r);
        return String.format("%s,%s,%s", i, r, c);
    }

    public static String getRandomStr() {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 6; i++) {
            int number = random.nextInt(str.length());
            char charAt = str.charAt(number);
            sb.append(charAt);
        }
        return sb.toString();
    }

}
