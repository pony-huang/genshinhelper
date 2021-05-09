package org.ponking.gih.server.param;

/**
 * @Author ponking
 * @Date 2021/5/4 16:47
 */
public class BaseParam {


    private String touser;

    private String toparty;

    private String totag;

    private String agentid;

    private Integer safe;

    private Integer enable_id_trans;

    private Integer enable_duplicate_check;


    private final String msgtype;

    public BaseParam(String msgtype) {
        this.msgtype = msgtype;
    }

    public String getTouser() {
        return touser;
    }

    public <T extends BaseParam> BaseParam setTouser(String touser) {
        this.touser = touser;
        return this;
    }

    public String getToparty() {
        return toparty;
    }

    public <T extends BaseParam> BaseParam setToparty(String toparty) {
        this.toparty = toparty;
        return this;
    }

    public String getTotag() {
        return totag;
    }

    public <T extends BaseParam> BaseParam setTotag(String totag) {
        this.totag = totag;
        return this;
    }

    public String getAgentid() {
        return agentid;
    }

    public <T extends BaseParam> BaseParam setAgentid(String agentid) {
        this.agentid = agentid;
        return this;
    }

    public Integer getSafe() {
        return safe;
    }

    public <T extends BaseParam> BaseParam setSafe(Integer safe) {
        this.safe = safe;
        return this;
    }

    public Integer getEnable_id_trans() {
        return enable_id_trans;
    }

    public <T extends BaseParam> BaseParam setEnable_id_trans(Integer enable_id_trans) {
        this.enable_id_trans = enable_id_trans;
        return this;
    }

    public Integer getEnable_duplicate_check() {
        return enable_duplicate_check;
    }

    public <T extends BaseParam> BaseParam setEnable_duplicate_check(Integer enable_duplicate_check) {
        this.enable_duplicate_check = enable_duplicate_check;
        return this;
    }

    public String getMsgtype() {
        return msgtype;
    }
}
