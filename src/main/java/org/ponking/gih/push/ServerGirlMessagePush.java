package org.ponking.gih.push;

import org.apache.http.Header;
import org.ponking.gih.util.HttpUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author ponking
 * @Date 2021/5/7 14:49
 */
public class ServerGirlMessagePush implements MessagePush {

    private final String scKey;

    public final String SERVER_GIRL = "https://sc.ftqq.com/%s.send";

    public ServerGirlMessagePush(String scKey) {
        this.scKey = scKey;
    }

    @Override
    public void sendMessage(String text, String desp) {
        Map<String, Object> data = new HashMap<>();
        data.put("text", text);
        data.put("desp", desp);
        HttpUtils.doGet(getServerGirl(), new Header[0], data);
    }

    public String getServerGirl() {
        return String.format(SERVER_GIRL, scKey);
    }

}
