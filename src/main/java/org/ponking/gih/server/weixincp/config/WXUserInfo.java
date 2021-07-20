package org.ponking.gih.server.weixincp.config;

import lombok.Data;

/**
 * @Author ponking
 * @Date 2021/7/20 23:36
 */
@Data
public class WXUserInfo {

    private String corpId;

    private String corpSecret;

    private String agentId;

    private String toUser;

    public WXUserInfo() {
    }

    public WXUserInfo(String corpId, String corpSecret, String agentId) {
        this(corpId, corpSecret, agentId, "@all");
    }

    public WXUserInfo(String corpId, String corpSecret, String agentId, String toUser) {
        this.corpId = corpId;
        this.corpSecret = corpSecret;
        this.agentId = agentId;
        this.toUser = toUser;
    }

    public String getToUser() {
        return toUser == null ? "@all" : toUser;
    }
}
