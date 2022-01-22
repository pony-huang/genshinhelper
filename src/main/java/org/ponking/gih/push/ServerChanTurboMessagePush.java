package org.ponking.gih.push;

import org.apache.http.Header;
import org.ponking.gih.util.HttpUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @Author ponking
 * @Date 2021/5/31 19:27
 */
public class ServerChanTurboMessagePush implements MessagePush {

    private final String scKey;

    public final String SERVER_GIRL = "https://sctapi.ftqq.com/%s.send?title=%s&desp=%s";

    public ServerChanTurboMessagePush(String scKey) {
        this.scKey = scKey;
    }

    @Override
    public void sendMessage(String title, String desp) {
        String url = getServerGirl(title, desp);
        HttpUtils.doPost(url, new Header[0], null);
    }

    public String getServerGirl(String title, String desp) {
        String encodeDesp = "";
        String encodeTitle = "";
        try {
            encodeDesp = URLEncoder.encode(desp, "UTF-8");
            encodeTitle = URLEncoder.encode(title, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return String.format(SERVER_GIRL, scKey, encodeTitle, encodeDesp);
    }
}
