package org.ponking.gih.gs;

import java.io.Serializable;
import java.util.List;

/**
 * @Author ponking
 * @Date 2021/6/1 14:31
 */
public class GenshinHelperProperties implements Serializable {

    private static final long serialVersionUID = 1L;

    private String mode;

    private String sckey;

    private String corpid;

    private String corpsecret;

    private String agentid;

    private List<Account> account;


    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getSckey() {
        return sckey;
    }

    public void setSckey(String sckey) {
        this.sckey = sckey;
    }

    public String getCorpid() {
        return corpid;
    }

    public void setCorpid(String corpid) {
        this.corpid = corpid;
    }

    public String getCorpsecret() {
        return corpsecret;
    }

    public void setCorpsecret(String corpsecret) {
        this.corpsecret = corpsecret;
    }

    public String getAgentid() {
        return agentid;
    }

    public void setAgentid(String agentid) {
        this.agentid = agentid;
    }

    public List<Account> getAccount() {
        return account;
    }

    public void setAccount(List<Account> account) {
        this.account = account;
    }


    public static class Account {

        private String cookie;

        private String stuid;

        private String stoken;

        public String getCookie() {
            return cookie;
        }

        public void setCookie(String cookie) {
            this.cookie = cookie;
        }

        public String getStuid() {
            return stuid;
        }

        public void setStuid(String stuid) {
            this.stuid = stuid;
        }

        public String getStoken() {
            return stoken;
        }

        public void setStoken(String stoken) {
            this.stoken = stoken;
        }
    }
}
