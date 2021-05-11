package org.ponking.gih.push;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.ponking.gih.GenShinConfig;
import org.ponking.gih.util.HttpUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

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
        try {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("text", text));
            params.add(new BasicNameValuePair("desp", desp));
            URI uri = new URIBuilder(getServerGirl()).setParameters(params).build();
            HttpUtils.doGet(uri, new Header[0]);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public String getServerGirl() {
        return String.format(SERVER_GIRL, scKey);
    }

}
