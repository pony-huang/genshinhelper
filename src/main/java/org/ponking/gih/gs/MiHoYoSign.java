package org.ponking.gih.gs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ponking.gih.gs.pojo.Post;
import org.ponking.gih.gs.pojo.PostResult;
import org.ponking.gih.util.HttpUtils;

import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * @Author ponking
 * @Date 2021/5/26 9:18
 */
public class MiHoYoSign extends AbstractSign {

    private static Logger logger = LogManager.getLogger(MiHoYoSign.class.getName());

    private final String forumId;

    public List<PostResult> postList = new ArrayList<>();

    public MiHoYoSign(String cookie, String forumId) {
        super(cookie);
        this.forumId = forumId;
    }

    @Override
    public void doSign() throws Exception {
        Random random = new Random();
        sign();
        getPosts();
        logger.info("看帖中...");
        IntStream.range(0, 3).forEach(
                item -> {
                    int index = random.nextInt(postList.size() - 1);
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
        logger.info("点赞中...");
        IntStream.range(0, 5).forEach(
                item -> {
                    int index = random.nextInt(postList.size() - 1);
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
        logger.info("分享中...");
        IntStream.range(0, 3).forEach(
                item -> {
                    int index = random.nextInt(postList.size() - 1);
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
        JSONObject signResult = HttpUtils.doPost(String.format(MiHoYoConfig.HUB_SIGN_URL, forumId), getMiHoYoHeaders(), null);
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
        JSONObject result = HttpUtils.doGet(String.format(MiHoYoConfig.HUB_LIST_URL, forumId), getMiHoYoHeaders());
        if ("OK".equals(result.get("message"))) {
            JSONArray jsonArray = result.getJSONObject("data").getJSONArray("list");
            postList = JSON.parseObject(JSON.toJSONString(jsonArray), new TypeReference<List<PostResult>>() {
            });
        }
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
        JSONObject result = HttpUtils.doGet(String.format(MiHoYoConfig.HUB_VIEW_URL, forumId), getMiHoYoHeaders(), data);
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
        JSONObject result = HttpUtils.doPost(MiHoYoConfig.HUB_VOTE_URL, getMiHoYoHeaders(), data);
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
        JSONObject result = HttpUtils.doGet(String.format(MiHoYoConfig.HUB_SHARE_URL, forumId), getMiHoYoHeaders());
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
}
