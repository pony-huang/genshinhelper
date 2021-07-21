package org.ponking.gih.push;


import org.ponking.gih.server.weixincp.service.PushMessageServiceImpl;
import org.ponking.gih.server.weixincp.service.WXUserInfo;
import org.ponking.gih.util.FileUtils;

/**
 * @Author ponking
 * @Date 2021/5/7 20:15
 */
public class WeixinCPMessagePush implements MessagePush {

    private final WXUserInfo userInfo;

    public WeixinCPMessagePush(WXUserInfo userInfo) {
        this.userInfo = userInfo;
    }


    @Override
    public void sendMessage(String title, String desp) {
        String log = FileUtils.loadDaily();
        if (log.length() <= 512) {
            sendMessageCardType(title, desp);
            return;
        }
        int start = 0;
        while (log.length() > start) { // 企业微信推送消息限制长度512
            String ms = log.substring(start, Math.min(start + 512, log.length()));
            sendMessageTexTType((start == 0 ? title + "\n" : "") + ms);
            start += 512;
        }
    }


    public void sendMessageCardType(String title, String desp) {
        PushMessageServiceImpl service = new PushMessageServiceImpl(userInfo);
        service.sendWithTextCard(title, desp);
    }

    public void sendMessageTexTType(String content) {
        PushMessageServiceImpl service = new PushMessageServiceImpl(userInfo);
        service.sendWithText(content);
    }

}
