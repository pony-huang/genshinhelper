package org.ponking.gih.server.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @Author ponking
 * @Date 2021/5/4 13:15
 */
public class WeixinCpConfig {

    private static WeiXinApp MY_APP = null;

    public static WeiXinApp getInstance() {
        if (MY_APP == null) {
            synchronized (WeiXinApp.class) {
                if (MY_APP == null) {
                    InputStream is = Thread.currentThread().getContextClassLoader()
                            .getResourceAsStream("weixinwork.properties");
                    Properties properties = new Properties();
                    try {
                        properties.load(is);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    WeiXinApp app = new WeiXinApp();
                    app.setCorpId((String) properties.get("corpid"));
                    app.setCorpSecret((String) properties.get("corpsecret"));
                    app.setAgentId((String) properties.get("agentid"));
                    MY_APP = app;
                }
            }
        }
        return MY_APP;
    }

    public static class WeiXinApp {

        private String corpId;

        private String corpSecret;

        private String agentId;

        public WeiXinApp() {
        }

        public WeiXinApp(String corpId, String corpSecret, String agentId) {
            this.corpId = corpId;
            this.corpSecret = corpSecret;
            this.agentId = agentId;
        }

        public String getCorpId() {
            return corpId;
        }

        public void setCorpId(String corpId) {
            this.corpId = corpId;
        }

        public String getCorpSecret() {
            return corpSecret;
        }

        public void setCorpSecret(String corpSecret) {
            this.corpSecret = corpSecret;
        }

        public String getAgentId() {
            return agentId;
        }

        public void setAgentId(String agentId) {
            this.agentId = agentId;
        }


        @Override
        public String toString() {
            return "WeiXinApp{" +
                    "corpId='" + corpId + '\'' +
                    ", corpSecret='" + corpSecret + '\'' +
                    ", agentId='" + agentId + '\'' +
                    '}';
        }
    }


}
