package org.ponking.gih.server.weixincp.param;

/**
 * @Author ponking
 * @Date 2021/6/1 18:29
 */
public class AppPushMarkdownParam extends BaseParam {

    private Markdown markdown;

    public AppPushMarkdownParam() {
        super("markdown");
    }


    public static AppPushMarkdownParam build(String content, String agentid) {
        AppPushMarkdownParam.Markdown t = new AppPushMarkdownParam.Markdown(content);
        AppPushMarkdownParam param = new AppPushMarkdownParam();
        param.setText(t).setToparty("@all").setTouser("@all").setTotag("@all").setAgentid(agentid).
                setEnable_duplicate_check(0).setEnable_id_trans(0).setSafe(0);
        return param;
    }

    public static class Markdown {
        private String content;

        public Markdown(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public Markdown getMarkdown() {
        return markdown;
    }

    public AppPushMarkdownParam setText(Markdown markdown) {
        this.markdown = markdown;
        return this;
    }
}
