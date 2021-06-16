package org.ponking.gih.push;


import org.ponking.gih.server.weixincp.config.PushMessageServiceImpl;
import org.ponking.gih.server.weixincp.config.WeixinCpConfig;
import org.ponking.gih.util.FileUtils;

/**
 * @Author ponking
 * @Date 2021/5/7 20:15
 */
public class WeixinCPMessagePush implements MessagePush {

    private final WeixinCpConfig.WeiXinApp myApp;

    public WeixinCPMessagePush(WeixinCpConfig.WeiXinApp myApp) {
        this.myApp = myApp;
    }


    @Override
    public void sendMessage(String text, String desp) {
        String log = FileUtils.loadDaily();
        int start = 0;
        while (log.length() > start) { // 企业微信推送消息限制长度512
            String ms = log.substring(start, Math.min(start + 512, log.length()));
            sendMessage((start == 0 ? "原神签到日志\n" : "") + ms);
            start += 512;
        }
    }


    public void sendMessage(String content) {
        PushMessageServiceImpl service = new PushMessageServiceImpl(myApp);
        service.sendWithText(content);
    }

}
