package org.ponking.gih.server.weixincp.result;

/**
 * @Author ponking
 * @Date 2021/5/4 23:40
 */
public class Result {

    private Integer errcode;

    private String errmsg;

    private String invaliduser;

    public Integer getErrcode() {
        return errcode;
    }

    public void setErrcode(Integer errcode) {
        this.errcode = errcode;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public String getInvaliduser() {
        return invaliduser;
    }

    public void setInvaliduser(String invaliduser) {
        this.invaliduser = invaliduser;
    }
}
