package org.ponking.gih.gs;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.util.*;

/**
 * @Author ponking
 * @Date 2021/5/26 10:15
 */
public abstract class AbstractSign {

    public final String cookie;

    public AbstractSign(String cookie) {
        this.cookie = cookie;
    }

    public abstract void doSign() throws Exception;

    protected Header[] getHeaders() {
        Header[] basicHeaders = getBasicHeaders();
        List<Header> newHeaders = new ArrayList<Header>();
        BasicHeader h1 = new BasicHeader("x-rpc-device_id", UUID.randomUUID().toString().toUpperCase());
        BasicHeader h2 = new BasicHeader("Content-Type", "application/json;charset=UTF-8");
        BasicHeader h3 = new BasicHeader("x-rpc-client_type", MiHoYoConfig.CLIENT_TYPE);
        BasicHeader h4 = new BasicHeader("x-rpc-app_version", MiHoYoConfig.APP_VERSION);
        BasicHeader h5 = new BasicHeader("DS", getDS());
        newHeaders.add(h1);
        newHeaders.add(h2);
        newHeaders.add(h3);
        newHeaders.add(h4);
        newHeaders.add(h5);
        newHeaders.addAll(new ArrayList<>(Arrays.asList(basicHeaders)));
        return newHeaders.toArray(new Header[0]);
    }

    protected Header[] getBasicHeaders() {
        BasicHeader h1 = new BasicHeader("Cookie", cookie);
        BasicHeader h2 = new BasicHeader("User-Agent", MiHoYoConfig.USER_AGENT);
        BasicHeader h3 = new BasicHeader("Referer", MiHoYoConfig.REFERER_URL);
        BasicHeader h4 = new BasicHeader("Accept-Encoding", "gzip, deflate, br");
        BasicHeader h5 = new BasicHeader("x-rpc-channel", "appstore");
        BasicHeader h6 = new BasicHeader("accept-language", "zh-CN,zh;q=0.9,ja-JP;q=0.8,ja;q=0.7,en-US;q=0.6,en;q=0.5");
        BasicHeader h7 = new BasicHeader("accept-encoding", "gzip, deflate");
        BasicHeader h8 = new BasicHeader("x-requested-with", "com.mihoyo.hyperion");
        BasicHeader h9 = new BasicHeader("Host", "api-takumi.mihoyo.com");
        return new ArrayList<>(Arrays.asList(h1, h2, h3, h4, h5, h6, h7, h8, h9)).toArray(new Header[0]);
    }

    protected String getDS() {
        String n = "h8w582wxwgqvahcdkpvdhbh2w9casgfl";
        String i = (System.currentTimeMillis() / 1000) + "";
        String r = getRandomStr();
        String c = DigestUtils.md5Hex("salt=" + n + "&t=" + i + "&r=" + r);
        return String.format("%s,%s,%s", i, r, c);
    }

    protected String getRandomStr() {
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
