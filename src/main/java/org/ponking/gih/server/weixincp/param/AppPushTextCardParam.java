package org.ponking.gih.server.weixincp.param;

import org.ponking.gih.server.weixincp.config.CardMessage;

/**
 * @Author ponking
 * @Date 2021/5/4 16:47
 */
public class AppPushTextCardParam extends BaseParam {

    private TextCard textcard;

    public AppPushTextCardParam() {
        super("textcard");
    }


    public static AppPushTextCardParam build(String agentid, CardMessage message) {
        TextCard t = new TextCard(message.getDescription(),
                message.getTitle(), message.getUrl(), message.getBtntxt());
        AppPushTextCardParam param = new AppPushTextCardParam();
        param.setTextcard(t).setToparty("@all").setTouser("@all").setTotag("@all").setAgentid(agentid).
                setEnable_duplicate_check(0).setEnable_id_trans(0).setSafe(0);
        return param;
    }

    public static AppPushTextCardParam build(String agentid, String description, String title, String url) {
        TextCard t = new TextCard(description, title, url, "更多");
        AppPushTextCardParam param = new AppPushTextCardParam();
        param.setTextcard(t).setToparty("@all").setTouser("@all").setTotag("@all").setAgentid(agentid).
                setEnable_duplicate_check(0).setEnable_id_trans(0).setSafe(0);
        return param;
    }


    public static AppPushTextCardParam build(String agentid, String description, String title, String url, String btntxt) {
        TextCard t = new TextCard(description, title, url, btntxt);
        AppPushTextCardParam param = new AppPushTextCardParam();
        param.setTextcard(t).setToparty("@all").setTouser("@all").setTotag("@all").setAgentid(agentid).
                setEnable_duplicate_check(0).setEnable_id_trans(0).setSafe(0);
        return param;
    }

    public static class TextCard {

        private String description;

        private String title;

        private String url;

        private String btntxt;

        public TextCard(String description, String title, String url) {
            this.description = description;
            this.title = title;
            this.url = url;
        }

        public TextCard(String description, String title, String url, String btntxt) {
            this.description = description;
            this.title = title;
            this.url = url;
            this.btntxt = btntxt;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getBtntxt() {
            return btntxt;
        }

        public void setBtntxt(String btntxt) {
            this.btntxt = btntxt;
        }
    }

    public TextCard getTextcard() {
        return textcard;
    }

    public AppPushTextCardParam setTextcard(TextCard card) {
        this.textcard = card;
        return this;
    }
}
