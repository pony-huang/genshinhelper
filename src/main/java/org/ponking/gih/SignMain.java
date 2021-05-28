package org.ponking.gih;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ponking.gih.gs.GenShinSign;
import org.ponking.gih.gs.MiHoYoSign;
import org.ponking.gih.push.MessagePush;
import org.ponking.gih.push.ServerGirlMessagePush;
import org.ponking.gih.push.WeixinCPMessagePush;
import org.ponking.gih.server.weixincp.config.WeixinCpConfig;
import org.ponking.gih.util.LoadLogFileResource;

import java.net.URISyntaxException;

/**
 * @Author ponking
 * @Date 2021/5/7 10:09
 */
public class SignMain {

    private static Logger logger = LogManager.getLogger(SignMain.class.getName());

    /**
     * 1. args[0]:cookie
     * 1. args[0]:cookie args[1]:scKey
     * 2. args[0]:cookie args[1]:corpid args[2]:corpsecret  args[3]:agentid
     *
     * @param args
     * @throws URISyntaxException
     */
    public static void main(String[] args) throws Exception {
        GenShinSign genShinSign;
        MiHoYoSign miHoYoSign;
        MessagePush messagePush = null;
        int length = args.length;
        if (length == 1) {
            genShinSign = new GenShinSign(args[0]);
        } else if (length == 2) {
            genShinSign = new GenShinSign(args[0]);
            messagePush = new ServerGirlMessagePush(args[1]);
        } else if (args.length == 4) {
            genShinSign = new GenShinSign(args[0]);
            WeixinCpConfig.WeiXinApp wconfig = new WeixinCpConfig.WeiXinApp(args[1], args[2], args[3]);
            messagePush = new WeixinCPMessagePush(wconfig);
        } else {
            throw new UnsupportedOperationException("参数异常");
        }
        logger.info("签到任务开始");
        try {
            if ("true".equals(System.getProperty("openHub"))) {
                miHoYoSign = new MiHoYoSign(args[0], "2");
                miHoYoSign.doSign();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        genShinSign.sign();
        logger.info("签到任务完成");
        if (length >= 2) {
            messagePush.sendMessage("原神签到日志", LoadLogFileResource.loadDailyFile());
        }
    }
}
