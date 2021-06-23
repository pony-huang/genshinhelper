package org.ponking.gih.gs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.ponking.gih.gs.pojo.PostResult;
import org.ponking.gih.util.HttpUtils;
import org.ponking.gih.util.LoggerUtils;

import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.*;

/**
 * @Author ponking
 * @Date 2021/5/26 9:18
 */
public class MiHoYoSign extends AbstractSign {

    private final MiHoYoConfig.Hub hub;

    private final String stuid;

    private final String stoken;

    private final Random random = new Random();

    /**
     * 浏览帖子数
     */
    private final static int VIEW_NUM = 5;

    /**
     * 点赞帖子数
     */
    private final static int UP_VOTE_NUM = 5;

    /**
     * 分享帖子数
     */
    private final static int SHARE_NUM = 3;

    public MiHoYoSign(MiHoYoConfig.Hub hub, String stuid, String stoken) {
        this(null, hub, stuid, stoken);
    }

    private final ExecutorService pool = new ThreadPoolExecutor(3, 3, 20,
            TimeUnit.SECONDS, new LinkedBlockingDeque<>(), Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());

    public MiHoYoSign(String cookie, MiHoYoConfig.Hub hub, String stuid, String stoken) {
        super(cookie);
        this.hub = hub;
        this.stuid = stuid;
        this.stoken = stoken;
    }

    @Override
    public void doSign() throws Exception {
        LoggerUtils.info("社区签到任务开始");
        sign();
        List<PostResult> genShinHomePosts = getGenShinHomePosts();
        List<PostResult> homePosts = getPosts();
        LoggerUtils.info("获取旅行者社区帖子成功，总共帖子数: {}", genShinHomePosts.size());
        //执行任务
        Future<Integer> vpf = pool.submit(createTask(this, "viewPost", VIEW_NUM, genShinHomePosts));
        Future<Integer> spf = pool.submit(createTask(this, "sharePost", SHARE_NUM, genShinHomePosts));
        Future<Integer> upf0 = pool.submit(createTask(this, "upVotePost", UP_VOTE_NUM, homePosts));
        Future<Integer> upf = pool.submit(createTask(this, "upVotePost", UP_VOTE_NUM, genShinHomePosts));
        //打印日志
        LoggerUtils.info("浏览帖子,成功: {},失败：{}", vpf.get(), VIEW_NUM - vpf.get());
        LoggerUtils.info("点赞帖子,成功: {},失败：{}", upf.get(), UP_VOTE_NUM - upf.get());
        LoggerUtils.info("分享帖子,成功: {},失败：{}", spf.get(), SHARE_NUM - spf.get());
        pool.shutdown();
        LoggerUtils.info("社区签到任务完成");
    }

    public Callable<Integer> createTask(Object obj, String methodName, int num, List<PostResult> posts) {
        return () -> {
            try {
                return doTask(obj, obj.getClass().getDeclaredMethod(methodName, PostResult.class), num, posts);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return 0;
        };
    }

    public int doTask(Object obj, Method method, int num, List<PostResult> posts) {
        int sc = 0;
        // 保证每个浏览(点赞，分享)的帖子不重复
        HashSet<Object> set = new HashSet<>(num);
        for (int i = 0; i < num; i++) {
            int index = 0;
            while (set.contains(index)) {
                index = random.nextInt(posts.size());
            }
            set.add(index);
            try {
                method.invoke(obj, posts.get(index));
                sc++;
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                TimeUnit.SECONDS.sleep(random.nextInt(2));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return sc;
    }


    /**
     * 原神签到
     */
    public void sign() {
        JSONObject signResult = HttpUtils.doPost(String.format(MiHoYoConfig.HUB_SIGN_URL, hub.getForumId()), getHubApiHeaders(), null);
        if ("OK".equals(signResult.get("message")) || "重复".equals(signResult.get("message"))) {
            LoggerUtils.info("社区签到: {}", signResult.get("message"));
        } else {
            LoggerUtils.info("社区签到失败: {}", signResult.get("message"));
        }
    }


    /**
     * 原神频道
     *
     * @throws Exception
     */
    public List<PostResult> getGenShinHomePosts() throws Exception {
        return getPosts(String.format(MiHoYoConfig.HUB_LIST1_URL, hub.getForumId()));
    }

    /**
     * 旅行者社区讨论区
     *
     * @throws Exception
     */
    public List<PostResult> getPosts() throws Exception {
        return getPosts(String.format(MiHoYoConfig.HUB_LIST2_URL, hub.getId()));
    }


    /**
     * 获取帖子
     *
     * @throws Exception
     */
    public List<PostResult> getPosts(String url) throws Exception {
        JSONObject result = HttpUtils.doGet(url, getHubApiHeaders());
        if ("OK".equals(result.get("message"))) {
            JSONArray jsonArray = result.getJSONObject("data").getJSONArray("list");
            List<PostResult> posts = JSON.parseObject(JSON.toJSONString(jsonArray), new TypeReference<List<PostResult>>() {
            });
//            LoggerUtils.info("获取帖子成功，总共帖子数: {}", posts.size());
            return posts;
        } else {
            throw new Exception("帖子数为空，请查配置并更新！！！");
        }
    }


    /**
     * 看帖
     *
     * @param post
     */
    public boolean viewPost(PostResult post) {
        Map<String, Object> data = new HashMap<>();
        data.put("post_id", post.getPost().getPost_id());
        data.put("is_cancel", false);
        JSONObject result = HttpUtils.doGet(String.format(MiHoYoConfig.HUB_VIEW_URL, hub.getForumId()), getDoTaskHubApiHeaders(), data);
        if ("OK".equals(result.get("message"))) {
//            LoggerUtils.info("看帖成功:{}", post.getPost().getSubject());
            return true;
        } else {
//            LoggerUtils.info("看帖失败:{}", result.get("message"));
            return false;
        }
    }

    /**
     * 点赞
     *
     * @param post
     */
    public boolean upVotePost(PostResult post) {
        Map<String, Object> data = new HashMap<>();
        data.put("post_id", post.getPost().getPost_id());
        data.put("is_cancel", false);
        JSONObject result = HttpUtils.doPost(MiHoYoConfig.HUB_VOTE_URL, getHubApiHeaders(), data);
        if ("OK".equals(result.get("message"))) {
//            LoggerUtils.info("点赞成功:{}", post.getPost().getSubject());
            return true;
        } else {
//            LoggerUtils.info("点赞失败:{}", result.get("message"));
            return false;
        }
    }

    /**
     * 分享
     *
     * @param post
     */
    public boolean sharePost(PostResult post) {
        JSONObject result = HttpUtils.doGet(String.format(MiHoYoConfig.HUB_SHARE_URL, hub.getForumId()), getDoTaskHubApiHeaders());
        if ("OK".equals(result.get("message"))) {
//            LoggerUtils.info("分享成功:{}", post.getPost().getSubject());
            return true;
        } else {
//            LoggerUtils.info("分享失败:{}", result.get("message"));
            return false;
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
            LoggerUtils.info("login_ticket已失效,请重新登录获取");
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


    public Header[] getDoTaskHubApiHeaders() {
        BasicHeader[] headers = new BasicHeader[20];
        headers[0] = new BasicHeader("x-rpc-client_type", "2");
        headers[1] = new BasicHeader("x-rpc-app_version", "2.8.0");
        headers[2] = new BasicHeader("x-rpc-sys_version", "10");
        headers[3] = new BasicHeader("x-rpc-channel", "miyousheluodi");
        headers[4] = new BasicHeader("x-rpc-device_id", UUID.randomUUID().toString().toLowerCase());
        headers[5] = new BasicHeader("x-rpc-device_name", "Xiaomi Redmi Note 4");
        headers[6] = new BasicHeader("Referer", "https://app.mihoyo.com");
        headers[7] = new BasicHeader("Content-Type", "application/json");
        headers[8] = new BasicHeader("Host", "bbs-api.mihoyo.com");
//        headers[9] = new BasicHeader("Content-Length", "41");
        headers[10] = new BasicHeader("Connection", "Keep-Alive");
        headers[11] = new BasicHeader("Accept-Encoding", "gzip");
        headers[12] = new BasicHeader("User-Agent", "okhttp/4.8.0");
        headers[13] = new BasicHeader("x-rpc-device_model", "Redmi Note 4");
        headers[13] = new BasicHeader("isLogin", "true");
        headers[14] = new BasicHeader("DS", getDS());
        headers[15] = new BasicHeader("cookie", "stuid=" + stuid + ";stoken=" + stoken + ";");
        return headers;
    }

}
