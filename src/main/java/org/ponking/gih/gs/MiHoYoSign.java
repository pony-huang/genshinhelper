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

import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.*;

/**
 * @Author ponking
 * @Date 2021/5/26 9:18
 */
public class MiHoYoSign extends AbstractSign {

    private static final Logger logger = LogManager.getLogger(MiHoYoSign.class.getName());

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

    public List<PostResult> postList = new ArrayList<>();

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
        sign();
        getPosts();
        // 创建任务
        Callable<Integer> viewPost = createTaskRunnable(this, "viewPost", VIEW_NUM);
        Callable<Integer> upVotePost = createTaskRunnable(this, "upVotePost", UP_VOTE_NUM);
        Callable<Integer> sharePost = createTaskRunnable(this, "sharePost", SHARE_NUM);
        //执行任务
        pool.submit(viewPost);
        pool.submit(upVotePost);
        pool.submit(sharePost);
        //打印日志
        logger.info("浏览帖子,成功: {},失败：{}", viewPost.call(), VIEW_NUM - viewPost.call());
        logger.info("点赞帖子,成功: {},失败：{}", upVotePost.call(), UP_VOTE_NUM - upVotePost.call());
        logger.info("分享帖子,成功: {},失败：{}", sharePost.call(), SHARE_NUM - sharePost.call());
    }

    public Callable<Integer> createTaskRunnable(Object obj, String methodName, int num) {
        return () -> {
            try {
                return doTask(obj, obj.getClass().getDeclaredMethod(methodName, PostResult.class), num);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            return 0;
        };
    }

    public int doTask(Object obj, Method method, int num) {
        int sc = 0;
        // 保证每个浏览(点赞，分享)的帖子不重复
        HashSet<Object> set = new HashSet<>(10);
        for (int i = 0; i < num; i++) {
            int index = 0;
            while (set.contains(index)) {
                index = random.nextInt(postList.size() - 1);
            }
            set.add(index);
            try {
                method.invoke(obj, postList.get(index));
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


    public void sign() {
        JSONObject signResult = HttpUtils.doPost(String.format(MiHoYoConfig.HUB_SIGN_URL, hub.getForumId()), getHubApiHeaders(), null);
        if ("OK".equals(signResult.get("message")) || "重复".equals(signResult.get("message"))) {
            logger.info("社区签到: {}", signResult.get("message"));
        } else {
            logger.info("社区签到失败: {}", signResult.get("message"));
        }
    }


    /**
     * 获取帖子
     *
     * @throws Exception
     */
    public void getPosts() throws Exception {
        JSONObject result = HttpUtils.doGet(String.format(MiHoYoConfig.HUB_LIST1_URL, hub.getForumId()), getHubApiHeaders());
        if ("OK".equals(result.get("message"))) {
            JSONArray jsonArray = result.getJSONObject("data").getJSONArray("list");
            postList = JSON.parseObject(JSON.toJSONString(jsonArray), new TypeReference<List<PostResult>>() {
            });
        } else {
            throw new Exception("帖子数为空，请查配置并更新！！！");
        }
        logger.info("获取帖子成功，总共帖子数: {}", postList.size());
    }

    /**
     * 获取帖子
     *
     * @return
     */
    public void getHomePosts() {
        JSONObject result = HttpUtils.doGet(String.format(MiHoYoConfig.HUB_LIST2_URL, hub.getId()), getHubApiHeaders());
        System.out.println(result);
        if ("OK".equals(result.get("message"))) {
            JSONArray jsonArray = result.getJSONObject("data").getJSONArray("list");
            postList = JSON.parseObject(JSON.toJSONString(jsonArray), new TypeReference<List<PostResult>>() {
            });
        }
        logger.info("获取帖子成功，总共帖子数: {}", postList.size());
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
        JSONObject result = HttpUtils.doGet(String.format(MiHoYoConfig.HUB_VIEW_URL, hub.getForumId()), getHubApiHeaders(), data);
        if ("OK".equals(result.get("message"))) {
//            logger.info("看帖成功:{}", post.getPost().getSubject());
            return true;
        } else {
//            logger.info("看帖失败:{}", result.get("message"));
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
//            logger.info("点赞成功:{}", post.getPost().getSubject());
            return true;
        } else {
//            logger.info("点赞失败:{}", result.get("message"));
            return false;
        }
    }

    /**
     * 分享
     *
     * @param post
     */
    public boolean sharePost(PostResult post) {
        JSONObject result = HttpUtils.doGet(String.format(MiHoYoConfig.HUB_SHARE_URL, hub.getForumId()), getHubApiHeaders());
        if ("OK".equals(result.get("message"))) {
//            logger.info("分享成功:{}", post.getPost().getSubject());
            return true;
        } else {
//            logger.info("分享失败:{}", result.get("message"));
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
