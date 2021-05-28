package org.ponking.gih.server.weixincp.param;

/**
 * @Author ponking
 * @Date 2021/5/4 16:47
 */
public class AppPushTextParam extends BaseParam{

    private Text text;

    public AppPushTextParam() {
        super("text");
    }

    public static AppPushTextParam build(String text,String agentid){
        Text t = new Text(text);
        AppPushTextParam param = new AppPushTextParam();
        param.setText(t).setToparty("@all").setTouser("@all").setTotag("@all").setAgentid(agentid).
                setEnable_duplicate_check(0).setEnable_id_trans(0).setSafe(0);
        return param;
    }

    public static class Text{
        private String content;

        public Text(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public Text getText() {
        return text;
    }

    public AppPushTextParam setText(Text text) {
        this.text = text;
        return this;
    }
}
