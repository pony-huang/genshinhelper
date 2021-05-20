package org.ponking.gih.gs;

/**
 * @Author ponking
 * @Date 2021/5/7 14:14
 */
public class GenShinConfig {


    /**
     * mihoyo genshin
     **/
    public static final String ACT_ID = "e202009291139501";

    public static final String APP_VERSION = "2.3.0";

    public static final String REGION = "cn_gf01";

    public static final String REFERER_URL = String.format("https://webstatic.mihoyo.com/bbs/event/signin-ys/index.html?bbs_auth_required=%s&act_id=%s&utm_source=%s&utm_medium=%s&utm_campaign=%s", true, ACT_ID, "bbs", "mys", "icon");

    public static final String AWARD_URL = String.format("https://api-takumi.mihoyo.com/event/bbs_sign_reward/home?act_id=%s", ACT_ID);

    public static final String ROLE_URL = String.format("https://api-takumi.mihoyo.com/binding/api/getUserGameRolesByCookie?game_biz=%s", "hk4e_cn");

    public static final String INFO_URL = "https://api-takumi.mihoyo.com/event/bbs_sign_reward/info";

    public static final String SIGN_URL = "https://api-takumi.mihoyo.com/event/bbs_sign_reward/sign";

    public static final String USER_AGENT = String.format("Mozilla/5.0 (iPhone; CPU iPhone OS 14_0_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) miHoYoBBS/%s", APP_VERSION);

}
