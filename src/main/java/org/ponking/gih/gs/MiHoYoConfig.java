package org.ponking.gih.gs;

/**
 * @Author ponking
 * @Date 2021/5/7 14:14
 */
public class MiHoYoConfig {


    /**
     * genshin
     **/
    public static final String ACT_ID = "e202009291139501"; // 切勿乱修改

    public static final String APP_VERSION = "2.3.0"; // 切勿乱修改

    public static final String CLIENT_TYPE = "5"; // 切勿乱修改

    public static final String REGION = "cn_gf01"; // 切勿乱修改

    public static final String REFERER_URL = String.format("https://webstatic.mihoyo.com/bbs/event/signin-ys/index.html?bbs_auth_required=%s&act_id=%s&utm_source=%s&utm_medium=%s&utm_campaign=%s", true, ACT_ID, "bbs", "mys", "icon");

    public static final String AWARD_URL = String.format("https://api-takumi.mihoyo.com/event/bbs_sign_reward/home?act_id=%s", ACT_ID);

    public static final String ROLE_URL = String.format("https://api-takumi.mihoyo.com/binding/api/getUserGameRolesByCookie?game_biz=%s", "hk4e_cn");

    public static final String INFO_URL = "https://api-takumi.mihoyo.com/event/bbs_sign_reward/info";

    public static final String SIGN_URL = "https://api-takumi.mihoyo.com/event/bbs_sign_reward/sign";

    public static final String USER_AGENT = String.format("Mozilla/5.0 (iPhone; CPU iPhone OS 14_0_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) miHoYoBBS/%s", APP_VERSION);


    /**
     * mihoyo hub
     */
    public static final String SIGN_IN_STATUS = "https://bbs-api.mihoyo.com/apihub/sapi/querySignInStatus";
    public static final String HUB_COOKIE1_URL = "https://webapi.account.mihoyo.com/Api/cookie_accountinfo_by_loginticket";
    public static final String HUB_COOKIE2_URL = "https://api-takumi.mihoyo.com/auth/api/getMultiTokenByLoginTicket?login_ticket=%s&token_types=3&uid=%s";
    public static final String HUB_SIGN_URL = "https://bbs-api.mihoyo.com/apihub/sapi/signIn?gids=%s";
    public static final String HUB_LIST_URL = "https://bbs-api.mihoyo.com/post/api/getForumPostList?forum_id=%s&is_good=false&is_hot=false&page_size=20&sort_type=1";
    public static final String HUB_VIEW_URL = "https://bbs-api.mihoyo.com/post/api/getPostFull?post_id=%s";
    public static final String HUB_SHARE_URL = "https://bbs-api.mihoyo.com/apihub/api/getShareConf?entity_id=%s&entity_type=1";
    public static final String HUB_VOTE_URL = "https://bbs-api.mihoyo.com/apihub/sapi/upvotePost";
    //https://bbs-api.mihoyo.com/notification/wapi/getUserGameUnreadCount?gids=2


    public static final Hub[] GAME_LIST = new Hub[]{
            new Hub.Builder().setId("1").setForumId("1").setName("崩坏3").setUrl("https://bbs.mihoyo.com/bh3/").build(),
            new Hub.Builder().setId("2").setForumId("26").setName("原神").setUrl("https://bbs.mihoyo.com/ys/").build(),
            new Hub.Builder().setId("3").setForumId("30").setName("崩坏2").setUrl("https://bbs.mihoyo.com/bh2/").build(),
            new Hub.Builder().setId("4").setForumId("37").setName("未定事件簿").setUrl("https://bbs.mihoyo.com/wd/").build(),
            new Hub.Builder().setId("5").setForumId("34").setName("大别野").setUrl("https://bbs.mihoyo.com/dby/").build()

    };


    public static class Hub {

        private String id;
        private String forumId;
        private String name;
        private String url;

        public Hub() {
        }

        private Hub(Builder builder) {
            this.id = builder.id;
            this.forumId = builder.forumId;
            this.name = builder.name;
            this.url = builder.url;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getForumId() {
            return forumId;
        }

        public void setForumId(String forumId) {
            this.forumId = forumId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public static class Builder {

            private String id;
            private String forumId;
            private String name;
            private String url;

            public Builder setId(String id) {
                this.id = id;
                return this;
            }

            public Builder setForumId(String forumId) {
                this.forumId = forumId;
                return this;
            }

            public Builder setName(String name) {
                this.name = name;
                return this;
            }

            public Builder setUrl(String url) {
                this.url = url;
                return this;
            }

            public Hub build() {
                return new Hub(this);
            }
        }
    }

}
