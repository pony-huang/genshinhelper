package org.ponking.gih.sign;

import org.ponking.gih.push.MessagePush;
import org.ponking.gih.push.ServerChanMessagePush;
import org.ponking.gih.push.WeixinCPMessagePush;
import org.ponking.gih.server.weixincp.config.WXUserInfo;
import org.ponking.gih.server.weixincp.config.WXUserInfoFactory;
import org.ponking.gih.sign.gs.GenShinSignMiHoYo;
import org.ponking.gih.sign.gs.GenshinHelperProperties;
import org.ponking.gih.sign.gs.MiHoYoConfig;
import org.ponking.gih.sign.gs.MiHoYoSignMiHoYo;

/**
 * @Author ponking
 * @Date 2021/5/31 15:54
 */
public class DailyTask {

    public GenShinSignMiHoYo genShinSign;

    public MiHoYoSignMiHoYo miHoYoSign;

    public MessagePush messagePush = null;

    public String[] args;

    public boolean pushed = false; // 是否推送日志


    /**
     * @param mode       推送消息方式
     * @param sckey      server酱sckey
     * @param corpid     企业微信corpid
     * @param corpsecret 企业微信corpsecret
     * @param agentid    企业微信agentid
     * @param account    账号配置信息
     */
    public DailyTask(String mode, String sckey, String corpid, String corpsecret, String agentid,
                     GenshinHelperProperties.Account account) {
        if (mode == null) {
            genShinSign = new GenShinSignMiHoYo(account.getCookie());
            if (account.getStuid() != null && account.getStoken() != null) {
                miHoYoSign = new MiHoYoSignMiHoYo(MiHoYoConfig.HubsEnum.YS.getGame(), account.getStuid(), account.getStoken());
            }
        } else if ("serverChan".equals(mode)) {
            genShinSign = new GenShinSignMiHoYo(account.getCookie());
            if (account.getStuid() != null && account.getStoken() != null) {
                miHoYoSign = new MiHoYoSignMiHoYo(MiHoYoConfig.HubsEnum.YS.getGame(), account.getStuid(), account.getStoken());
            }
            messagePush = new ServerChanMessagePush(sckey);
            pushed = true;
        } else if ("weixincp".equals(mode)) {
            genShinSign = new GenShinSignMiHoYo(account.getCookie());
            if (account.getStuid() != null && account.getStoken() != null) {
                miHoYoSign = new MiHoYoSignMiHoYo(MiHoYoConfig.HubsEnum.YS.getGame(), account.getStuid(), account.getStoken());
            }
            WXUserInfo wxUserInfo = new WXUserInfo(corpid, corpsecret, agentid, account.getToUser());
            messagePush = new WeixinCPMessagePush(wxUserInfo);
            pushed = true;
        } else {
            throw new UnsupportedOperationException("参数异常");
        }
    }


    public void doDailyTask() throws Exception {
        if (miHoYoSign != null) {
            miHoYoSign.doSign();
        }
        if (genShinSign != null) {
            genShinSign.sign();
        }
    }

    public MessagePush getMessagePush() {
        return messagePush;
    }

    public boolean isPushed() {
        return pushed;
    }
}
