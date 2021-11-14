package org.ponking.gih.sign;

import lombok.SneakyThrows;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ponking.gih.push.MessagePush;
import org.ponking.gih.push.ServerChanMessagePush;
import org.ponking.gih.push.ServerChanTurboMessagePush;
import org.ponking.gih.push.WeixinCPMessagePush;
import org.ponking.gih.server.weixincp.service.WXUserInfo;
import org.ponking.gih.sign.gs.GenShinSignMiHoYo;
import org.ponking.gih.sign.gs.GenshinHelperProperties;
import org.ponking.gih.sign.gs.MiHoYoConfig;
import org.ponking.gih.sign.gs.MiHoYoSignMiHoYo;
import org.ponking.gih.util.FileUtils;
import org.ponking.gih.util.StringUtils;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;


/**
 * @Author ponking
 * @Date 2021/5/31 15:54
 */
public class DailyTask implements Runnable {

    private static final Logger log = LogManager.getLogger(DailyTask.class);

    public GenShinSignMiHoYo genShinSign;

    public MiHoYoSignMiHoYo miHoYoSign;

    public MessagePush messagePush;

    private String[] signMode;

    private String logFilePath = null;

    private DailyTask(DailyTaskBuilder builder) {
        this(builder.genShinSign, builder.miHoYoSign, builder.messagePush, builder.signMode);
    }

    private DailyTask(GenShinSignMiHoYo genShinSign, MiHoYoSignMiHoYo miHoYoSign, MessagePush messagePush) {
        init();
        this.genShinSign = genShinSign;
        this.miHoYoSign = miHoYoSign;
        this.messagePush = messagePush;
    }

    private DailyTask(GenShinSignMiHoYo genShinSign, MiHoYoSignMiHoYo miHoYoSign,
                      MessagePush messagePush, String[] signMode) {
        init();
        this.genShinSign = genShinSign;
        this.miHoYoSign = miHoYoSign;
        this.messagePush = messagePush;
        this.signMode = signMode;
    }

    /**
     * @param mode       推送消息方式
     * @param sckey      server酱sckey
     * @param corpid     企业微信corpid
     * @param corpsecret 企业微信corpsecret
     * @param agentid    企业微信agentid
     * @param account    账号配置信息
     */
    @Deprecated
    public DailyTask(String mode, String sckey, String corpid, String corpsecret, String agentid,
                     GenshinHelperProperties.Account account) {
        init();
        if (mode == null) {
            genShinSign = new GenShinSignMiHoYo(account.getCookie());
            if (account.getStuid() != null && account.getStoken() != null) {
                miHoYoSign = new MiHoYoSignMiHoYo(MiHoYoConfig.HubsEnum.YS.getGame(), account.getStuid(), account.getStoken());
            }
        } else if (Constant.MODE_SERVER_CHAN.equals(mode)) {
            genShinSign = new GenShinSignMiHoYo(account.getCookie());
            if (account.getStuid() != null && account.getStoken() != null) {
                miHoYoSign = new MiHoYoSignMiHoYo(MiHoYoConfig.HubsEnum.YS.getGame(), account.getStuid(), account.getStoken());
            }
            messagePush = new ServerChanMessagePush(sckey);
        } else if (Constant.MODE_SERVER_TURBO_CHAN.equals(mode)) {
            genShinSign = new GenShinSignMiHoYo(account.getCookie());
            if (account.getStuid() != null && account.getStoken() != null) {
                miHoYoSign = new MiHoYoSignMiHoYo(MiHoYoConfig.HubsEnum.YS.getGame(), account.getStuid(), account.getStoken());
            }
            messagePush = new ServerChanTurboMessagePush(sckey);
        } else if (Constant.MODE_WEIXIN_CP_CHAN.equals(mode)) {
            genShinSign = new GenShinSignMiHoYo(account.getCookie());
            if (account.getStuid() != null && account.getStoken() != null) {
                miHoYoSign = new MiHoYoSignMiHoYo(MiHoYoConfig.HubsEnum.YS.getGame(), account.getStuid(), account.getStoken());
            }
            WXUserInfo wxUserInfo = new WXUserInfo(corpid, corpsecret, agentid, account.getToUser());
            messagePush = new WeixinCPMessagePush(wxUserInfo);
        } else {
            throw new UnsupportedOperationException("参数异常");
        }
    }

    private void init() {
        // 默认目录,因为云腾讯函数，只能在/tmp有读取日志权限，故手动设置腾讯云函数使用/tmp
        if (System.getProperty(Constant.GENSHIN_ENV_LOG_PATH).equals(Constant.ENV_TENCENT_LOG_PATH)) {
            this.logFilePath = Constant.ENV_TENCENT_LOG_PATH;
        } else {
            String baseDir = System.getProperty("user.dir");
            this.logFilePath = baseDir + File.separator + "logs";
        }
    }


    @SneakyThrows
    @Override
    public void run() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        log.info("开始执行时间[ {} ],执行环境[ {} ]", dtf.format(LocalDateTime.now()), System.getProperty(Constant.GENSHIN_EXEC));
        work();
    }

    public void doDailyTask(CountDownLatch countDownLatch) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        log.info("开始执行时间[ {} ],执行环境[ {} ]", dtf.format(LocalDateTime.now()), System.getProperty(Constant.GENSHIN_EXEC));
        work();
        countDownLatch.countDown();
    }

    private void work() {
        if (Objects.nonNull(miHoYoSign)) {
            try {
                if (Objects.nonNull(signMode)) {
                    String[] arr = this.signMode;
                    for (String val : arr) {
                        String type = val.toUpperCase();
                        if (type.equals(MiHoYoConfig.HubsEnum.YS.toString())) {
                            miHoYoSign.reSetHub(MiHoYoConfig.HubsEnum.YS.getGame());
                        } else if (type.equals(MiHoYoConfig.HubsEnum.BH3.toString())) {
                            miHoYoSign.reSetHub(MiHoYoConfig.HubsEnum.BH3.getGame());
                        } else if (type.equals(MiHoYoConfig.HubsEnum.DBY.toString())) {
                            miHoYoSign.reSetHub(MiHoYoConfig.HubsEnum.DBY.getGame());
                        } else if (type.equals(MiHoYoConfig.HubsEnum.WD.toString())) {
                            miHoYoSign.reSetHub(MiHoYoConfig.HubsEnum.WD.getGame());
                        } else {
                            continue;
                        }
                        miHoYoSign.doSingleThreadSign();
                    }
                } else {
                    miHoYoSign.doSingleThreadSign();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (Objects.nonNull(genShinSign)) {
            genShinSign.doSign();
        }
        if (Objects.nonNull(messagePush)) {
            String fileName = Thread.currentThread().getName() + ".log";
            //System.getProperty(Constant.GENSHIN_ENV_LOG_PATH) 根据执行（腾讯云）环境，获取日志文件路径
            messagePush.sendMessage("原神签到", FileUtils.loadDaily(logFilePath + File.separator + fileName));
        }
    }

    public static DailyTaskBuilder builder() {
        return new DailyTaskBuilder();
    }


    public static class DailyTaskBuilder {

        private GenShinSignMiHoYo genShinSign = null;

        private MiHoYoSignMiHoYo miHoYoSign = null;

        private MessagePush messagePush = null;

        private String[] signMode;

        public DailyTaskBuilder account(String cookie) {
            genShinSign = new GenShinSignMiHoYo(cookie);
            return this;
        }

        public DailyTaskBuilder msgPush(String mode, String scKey, String... params) {
            switch (mode) {
                case Constant.MODE_SERVER_CHAN: {
                    if (StringUtils.isBank(scKey)) {
                        throw new RuntimeException("参数有误");
                    }
                    messagePush = new ServerChanMessagePush(scKey);
                    break;
                }
                case Constant.MODE_SERVER_TURBO_CHAN: {
                    if (StringUtils.isBank(scKey)) {
                        throw new RuntimeException("参数有误");
                    }
                    messagePush = new ServerChanTurboMessagePush(scKey);
                    break;
                }
                case Constant.MODE_WEIXIN_CP_CHAN: {
                    WXUserInfo wxUserInfo = new WXUserInfo(params[0], params[1], params[2], params.length != 4 ? null : params[3]);
                    messagePush = new WeixinCPMessagePush(wxUserInfo);
                    break;
                }
                default:
                    break;
            }
            return this;
        }


        public DailyTaskBuilder miHoYoSign(MiHoYoConfig.HubsEnum type, String stuid, String stoken) {
            if (StringUtils.isBank(stuid) || StringUtils.isBank(stoken)) {
                throw new RuntimeException("参数有误");
            }
            miHoYoSign = new MiHoYoSignMiHoYo(type.getGame(), stuid, stoken);
            return this;
        }

        public DailyTaskBuilder signMode(String signMode) {
            if (StringUtils.isBank(signMode)) {
                throw new RuntimeException("参数有误");
            }
            this.signMode = signMode.split(",");
            return this;
        }


        /**
         * 默认原神
         *
         * @param account
         * @return
         */
        public DailyTaskBuilder miHoYoSign(GenshinHelperProperties.Account account) {
            if (StringUtils.isBank(account.getStuid()) || StringUtils.isBank(account.getStoken())) {
                throw new RuntimeException("参数有误");
            }
            miHoYoSign = new MiHoYoSignMiHoYo(MiHoYoConfig.HubsEnum.YS.getGame(), account.getStuid(), account.getStoken());
            return this;
        }

        public DailyTaskBuilder miHoYoSign(MiHoYoConfig.HubsEnum type, GenshinHelperProperties.Account account) {
            if (StringUtils.isBank(account.getStuid()) || StringUtils.isBank(account.getStoken())) {
                throw new RuntimeException("参数有误");
            }
            miHoYoSign = new MiHoYoSignMiHoYo(type.getGame(), account.getStuid(), account.getStoken());
            return this;
        }

        public DailyTaskBuilder miHoYoSignHub(MiHoYoConfig.HubsEnum type, GenshinHelperProperties.Account account) {
            if (StringUtils.isBank(account.getStuid()) || StringUtils.isBank(account.getStoken())) {
                throw new RuntimeException("参数有误");
            }
            miHoYoSign = new MiHoYoSignMiHoYo(type.getGame(), account.getStuid(), account.getStoken());
            return this;
        }

        public DailyTaskBuilder miHoYoSignHub(MiHoYoConfig.HubsEnum type, String stuid, String stoken) {
            if (StringUtils.isBank(stuid) || StringUtils.isBank(stoken)) {
                throw new RuntimeException("参数有误");
            }
            miHoYoSign = new MiHoYoSignMiHoYo(type.getGame(), stuid, stoken);
            return this;
        }

        public DailyTask build() {
            return new DailyTask(this);
        }
    }
}
