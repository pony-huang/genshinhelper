package org.ponking.gih.gs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ponking.gih.push.MessagePush;
import org.ponking.gih.push.ServerChanMessagePush;
import org.ponking.gih.push.WeixinCPMessagePush;
import org.ponking.gih.server.weixincp.config.WeixinCpConfig;

/**
 * @Author ponking
 * @Date 2021/5/31 15:54
 */
public class DailyTask {

    private static final Logger logger = LogManager.getLogger(DailyTask.class.getName());
    public GenShinSign genShinSign;
    public MiHoYoSign miHoYoSign;
    public MessagePush messagePush = null;
    public String[] args;
    public boolean pushed = false; // 是否推送日志

    public DailyTask(String[] args) {
        this.args = args;
        int length = args.length;
        if (length > 3) {
            pushed = true;
        }
        if (length == 3) {
            genShinSign = new GenShinSign(args[0]);
            miHoYoSign = new MiHoYoSign(MiHoYoConfig.HubsEnum.YS.getGame(), args[1], args[2]);
        } else if (length == 4) {
            genShinSign = new GenShinSign(args[0]);
            miHoYoSign = new MiHoYoSign(MiHoYoConfig.HubsEnum.YS.getGame(), args[1], args[2]);
            messagePush = new ServerChanMessagePush(args[3]);
        } else if (args.length == 6) {
            genShinSign = new GenShinSign(args[0]);
            WeixinCpConfig.WeiXinApp wconfig = new WeixinCpConfig.WeiXinApp(args[3], args[4], args[5]);
            miHoYoSign = new MiHoYoSign(MiHoYoConfig.HubsEnum.YS.getGame(), args[1], args[2]);
            messagePush = new WeixinCPMessagePush(wconfig);
        } else {
            throw new UnsupportedOperationException("参数异常");
        }
    }

    /**
     * @param mode       推送消息方式
     * @param sckey      server酱sckey
     * @param corpid     企业微信corpid
     * @param corpsecret 企业微信corpsecret
     * @param agentid    企业微信agentid
     * @param account    米哈游账号信息
     */
    public DailyTask(String mode, String sckey, String corpid, String corpsecret, String agentid, GenshinHelperProperties.Account account) {
        if (mode == null) {
            genShinSign = new GenShinSign(account.getCookie());
            miHoYoSign = new MiHoYoSign(MiHoYoConfig.HubsEnum.YS.getGame(), account.getStuid(), account.getStoken());
        } else if ("serverChan".equals(mode)) {
            genShinSign = new GenShinSign(account.getCookie());
            miHoYoSign = new MiHoYoSign(MiHoYoConfig.HubsEnum.YS.getGame(), account.getStuid(), account.getStoken());
            messagePush = new ServerChanMessagePush(sckey);
            pushed = true;
        } else if ("weixincp".equals(mode)) {
            genShinSign = new GenShinSign(account.getCookie());
            miHoYoSign = new MiHoYoSign(MiHoYoConfig.HubsEnum.YS.getGame(), account.getStuid(), account.getStoken());
            WeixinCpConfig.WeiXinApp wconfig = new WeixinCpConfig.WeiXinApp(corpid, corpsecret, agentid);
            messagePush = new WeixinCPMessagePush(wconfig);
            pushed = true;
        } else {
            throw new UnsupportedOperationException("参数异常");
        }
    }


    public void doDailyTask() throws Exception {
        logger.info("签到任务开始");
        miHoYoSign.doSign();

        logger.info("原神福利签到开始");
        genShinSign.sign();

        logger.info("原神福利签到完成");

        logger.info("签到任务完成");
    }

    public MessagePush getMessagePush() {
        return messagePush;
    }

    public boolean isPushed() {
        return pushed;
    }
}
