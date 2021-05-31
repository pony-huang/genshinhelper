package org.ponking.gih.gs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ponking.gih.gs.pojo.PostResult;
import org.ponking.gih.util.HttpUtils;

import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @Author ponking
 * @Date 2021/5/26 9:18
 */
public class MiHoYoSign extends AbstractSign {

    private static final Logger logger = LogManager.getLogger(MiHoYoSign.class.getName());

    private final String forumId;

    private final String stuid;

    private final String stoken;

    public List<PostResult> postList = new ArrayList<>();

    public MiHoYoSign(String forumId, String stuid, String stoken) {
        this(null, forumId, stuid, stoken);
    }

    public MiHoYoSign(String cookie, String forumId, String stuid, String stoken) {
        super(cookie);
        this.forumId = forumId;
        this.stuid = stuid;
        this.stoken = stoken;
    }

    @Override
    public void doSign() throws Exception {
        Random random = new Random();
        HashSet<Object> set = new HashSet<>(10); // 保证每个浏览(点赞，分享)的帖子不重复
        sign();
        getPosts();
        logger.info("-->> 看帖中...");
        IntStream.range(0, 3).forEach(
                item -> {
                    int index = 0;
                    while (set.contains(index)) {
                        index = random.nextInt(postList.size() - 1);
                    }
                    set.add(index);
                    try {
                        viewPost(postList.get(index));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        TimeUnit.SECONDS.sleep(random.nextInt(3));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        );
        set.clear();
        logger.info("-->> 点赞中...");
        IntStream.range(0, 5).forEach(
                item -> {
                    int index = 0;
                    while (set.contains(index)) {
                        index = random.nextInt(postList.size() - 1);
                    }
                    set.add(index);
                    try {
                        upVotePost(postList.get(index));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        TimeUnit.SECONDS.sleep(random.nextInt(3));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        );
        logger.info("-->> 分享中...");
        set.clear();
        IntStream.range(0, 3).forEach(
                item -> {
                    int index = 0;
                    while (set.contains(index)) {
                        index = random.nextInt(postList.size() - 1);
                    }
                    set.add(index);
                    try {
                        sharePost(postList.get(index));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        TimeUnit.SECONDS.sleep(random.nextInt(3));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        );
    }


    public void sign() throws Exception {
        JSONObject signResult = HttpUtils.doPost(String.format(MiHoYoConfig.HUB_SIGN_URL, forumId), getHubApiHeaders(), null);
        if ("OK".equals(signResult.get("message")) || "重复".equals(signResult.get("message"))) {
            logger.info("社区签到: {}", signResult.get("message"));
        } else {
            logger.info("社区签到失败: {}", signResult.get("message"));
        }
    }


    /**
     * 获取帖子
     *
     * @return
     * @throws Exception
     */
    public List<PostResult> getPosts() throws Exception {
        JSONObject result = HttpUtils.doGet(String.format(MiHoYoConfig.HUB_LIST1_URL, forumId), getHubApiHeaders());
        if ("OK".equals(result.get("message"))) {
            JSONArray jsonArray = result.getJSONObject("data").getJSONArray("list");
            postList = JSON.parseObject(JSON.toJSONString(jsonArray), new TypeReference<List<PostResult>>() {
            });
        } else {
            throw new Exception("帖子数为空，请查配置并更新！！！");
        }
        logger.info("获取帖子成功，总共帖子数: {}", postList.size());
        return postList;
    }

    /**
     * 获取帖子
     *
     * @return
     * @throws Exception
     */
    public List<PostResult> getPosts2() throws Exception {
        JSONObject result = HttpUtils.doGet(String.format(MiHoYoConfig.HUB_LIST2_URL, forumId), getHubApiHeaders());
        if ("OK".equals(result.get("message"))) {
            JSONArray jsonArray = result.getJSONObject("data").getJSONArray("list");
            postList = JSON.parseObject(JSON.toJSONString(jsonArray), new TypeReference<List<PostResult>>() {
            });
        }
        logger.info("获取帖子成功，总共帖子数: {}", postList.size());
        return postList;
    }

    /**
     * 看帖
     *
     * @param post
     * @throws Exception
     */
    public void viewPost(PostResult post) throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("post_id", post.getPost().getPost_id());
        data.put("is_cancel", false);
        JSONObject result = HttpUtils.doGet(String.format(MiHoYoConfig.HUB_VIEW_URL, forumId), getHubApiHeaders(), data);
        if ("OK".equals(result.get("message"))) {
            logger.info("看帖成功:{}", post.getPost().getSubject());
        } else {
            logger.info("看帖失败:{}", result.get("message"));
        }
    }

    /**
     * 点赞
     *
     * @param post
     * @throws Exception
     */
    public void upVotePost(PostResult post) throws Exception {
        Map<String, Object> data = new HashMap<>();
        data.put("post_id", post.getPost().getPost_id());
        data.put("is_cancel", false);
        JSONObject result = HttpUtils.doPost(MiHoYoConfig.HUB_VOTE_URL, getHubApiHeaders(), data);
        if ("OK".equals(result.get("message"))) {
            logger.info("点赞成功:{}", post.getPost().getSubject());
        } else {
            logger.info("点赞失败:{}", result.get("message"));
        }
    }

    /**
     * 分享
     *
     * @param post
     * @throws Exception
     */
    public void sharePost(PostResult post) throws Exception {
        JSONObject result = HttpUtils.doGet(String.format(MiHoYoConfig.HUB_SHARE_URL, forumId), getHubApiHeaders());
        if ("OK".equals(result.get("message"))) {
            logger.info("分享成功:{}", post.getPost().getSubject());
        } else {
            logger.info("分享失败:{}", result.get("message"));
        }
    }


    /**
     * 获取 stoken
     *
     * @throws URISyntaxException
     */
    public String getCookieToken() throws Exception {
        JSONObject result = HttpUtils.
                doGet(String.format(MiHoYoConfig.HUB_COOKIE2_URL, getCookieByName("login_ticket"), getCookieByName("account_id")), getHeaders());
        if (!"OK".equals(result.get("message"))) {
            logger.info("login_ticket已失效,请重新登录获取");
            throw new Exception("login_ticket已失效,请重新登录获取");
        }
        return (String) result.getJSONObject("data").getJSONArray("list").getJSONObject(0).get("token");
    }

    public String getCookieByName(String name) {
        String[] split = cookie.split(";");
        for (String s : split) {
            String h = s.trim();
            if (h.startsWith(name)) {
                return h.substring(h.indexOf('=') + 1);
            }
        }
        return null;
    }


    @Deprecated
    public Header[] getMiHoYoHeaders() throws Exception {
        Header[] sH = getHeaders();
        BasicHeader[] newHeaders = new BasicHeader[sH.length + 1];
        String stuid = getCookieByName("account_id");
        String stoken = getCookieToken();
        BasicHeader h1 = new BasicHeader("cookie", "stuid=" + stuid + ";stoken=" + stoken + ";");
        System.arraycopy(sH, 0, newHeaders, 0, sH.length);
        newHeaders[newHeaders.length - 1] = h1;
        return newHeaders;
    }

    public Header[] getHubApiHeaders() {
        Header[] sH = getHeaders();
        BasicHeader[] newHeaders = new BasicHeader[sH.length + 1];
        BasicHeader h1 = new BasicHeader("cookie", "stuid=" + stuid + ";stoken=" + stoken + ";");
        System.arraycopy(sH, 0, newHeaders, 0, sH.length);
        newHeaders[newHeaders.length - 1] = h1;
        return newHeaders;
    }
}
