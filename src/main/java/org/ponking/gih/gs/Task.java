package org.ponking.gih.gs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ponking.gih.push.MessagePush;
import org.ponking.gih.push.ServerChanMessagePush;
import org.ponking.gih.push.WeixinCPMessagePush;
import org.ponking.gih.server.weixincp.config.WeixinCpConfig;
import org.ponking.gih.util.LoadLogFileResource;

/**
 * @Author ponking
 * @Date 2021/5/31 15:54
 */
public class Task {

    private static final Logger logger = LogManager.getLogger(Task.class.getName());
    public GenShinSign genShinSign;
    public MiHoYoSign miHoYoSign;
    public MessagePush messagePush = null;
    public String[] args;

    public Task(String[] args) {
        this.args = args;
        int length = args.length;
        if (length == 3) {
            genShinSign = new GenShinSign(args[0]);
            miHoYoSign = new MiHoYoSign(MiHoYoConfig.HubsEnum.YS.getGame().getForumId(), args[1], args[2]);
        } else if (length == 4) {
            genShinSign = new GenShinSign(args[0]);
            miHoYoSign = new MiHoYoSign(MiHoYoConfig.HubsEnum.YS.getGame().getForumId(), args[1], args[2]);
            messagePush = new ServerChanMessagePush(args[3]);
        } else if (args.length == 6) {
            genShinSign = new GenShinSign(args[0]);
            WeixinCpConfig.WeiXinApp wconfig = new WeixinCpConfig.WeiXinApp(args[3], args[4], args[5]);
            miHoYoSign = new MiHoYoSign(MiHoYoConfig.HubsEnum.YS.getGame().getForumId(), args[1], args[2]);
            messagePush = new WeixinCPMessagePush(wconfig);
        } else {
            throw new UnsupportedOperationException("参数异常");
        }
    }


    public void doDailyTask() throws Exception {
        int length = args.length;
        logger.info("签到任务开始");
        miHoYoSign.doSign();
        logger.info("-->> 原神福利签到开始...");
        genShinSign.sign();
        logger.info("签到任务完成");
        if (length > 3) {
            messagePush.sendMessage("原神签到日志", LoadLogFileResource.loadDailyFile());
        }
    }
}
