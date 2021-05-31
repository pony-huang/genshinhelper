package org.ponking.gih.push;


import org.ponking.gih.server.weixincp.config.PushMessageServiceImpl;
import org.ponking.gih.server.weixincp.config.WeixinCpConfig;

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
        PushMessageServiceImpl service = new PushMessageServiceImpl(myApp);
        service.sendWithText(text + "\n" + desp);
    }

}
