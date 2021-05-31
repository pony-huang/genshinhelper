package org.ponking.gih.push;

import org.apache.http.Header;
import org.ponking.gih.util.HttpUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author ponking
 * @Date 2021/5/31 19:27
 */
public class ServerChanTurboMessagePush implements MessagePush {

    private final String scKey;

    public final String SERVER_GIRL = "https://sct.ftqq.com/%s.send?text=%s&desp=%s";

    public ServerChanTurboMessagePush(String scKey) {
        this.scKey = scKey;
    }

    @Override
    public void sendMessage(String text, String desp) {
        Map<String, Object> data = new HashMap<>();
        data.put("text", text);
        data.put("desp", desp);
        HttpUtils.doGet(getServerGirl(text, desp), new Header[0], null);
    }

    public String getServerGirl(String text, String desp) {
        return String.format(SERVER_GIRL, scKey, text, desp);
    }
}
