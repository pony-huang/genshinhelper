package org.ponking.gih.gs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ponking.gih.util.HttpUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

/**
 * @Author ponking
 * @Date 2021/5/7 10:10
 */
public class Sign {

    private static Logger logger = LogManager.getLogger(Sign.class.getName());


    private final String cookie;

    public Sign(String cookie) {
        this.cookie = cookie;
    }

    public void sign() throws IOException, URISyntaxException {
        String uid = getUid();
//        if (isSigned(uid)) {
//            logger.info("已经签到！无需在签到...");
//            return;
//        }
        isSigned(uid);
        doSign(uid);
    }

    public String getUid() {
        JSONObject result = HttpUtils.doGet(GenShinConfig.ROLE_URL, getHeaders());
        String uid = (String) result.getJSONObject("data").getJSONArray("list").getJSONObject(0).get("game_uid");
        String nickname = (String) result.getJSONObject("data").getJSONArray("list").getJSONObject(0).get("nickname");
        logger.info("获取用户UID：{}", uid);
        logger.info("当前用户名称：{}", nickname);
        return uid;
    }

    public Award getAwardInfo(int day) {
        Map<String, String> data = new HashMap<>();
        data.put("act_id", GenShinConfig.ACT_ID);
        data.put("region", GenShinConfig.REGION);
        JSONObject awardResult = HttpUtils.doGet(GenShinConfig.AWARD_URL, getHeadersWithDeviceId());
        JSONArray jsonArray = awardResult.getJSONObject("data").getJSONArray("awards");
        List<Award> awards = JSON.parseObject(JSON.toJSONString(jsonArray), new TypeReference<List<Award>>() {
        });
        return awards.get(day - 1);
    }

    public boolean isSigned(String uid) throws URISyntaxException {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("act_id", GenShinConfig.ACT_ID));
        params.add(new BasicNameValuePair("region", GenShinConfig.REGION));
        params.add(new BasicNameValuePair("uid", uid));
        URI uri = new URIBuilder(GenShinConfig.INFO_URL)
                .setParameters(params).build();
        JSONObject signInfoResult = HttpUtils.doGet(uri, getHeadersWithDeviceId());

        LocalDateTime time = LocalDateTime.now();
        Boolean isSign = signInfoResult.getJSONObject("data").getBoolean("is_sign");
        Integer totalSignDay = signInfoResult.getJSONObject("data").getInteger("total_sign_day");
        int day = isSign ? totalSignDay : totalSignDay + 1;
        Award award = getAwardInfo(day);

        logger.info("{}月已签到{}天", time.getMonth().getValue(), totalSignDay);
        logger.info("今天{}签到可获取{}{}", signInfoResult.getJSONObject("data").get("today"), award.getCnt(), award.getName());
//        logger.info("是否已签到:" + signInfoResult.getJSONObject("data").getBoolean("is_sign"));
        return isSign;
    }


    public void doSign(String uid) {
        Map<String, String> data = new HashMap<>();
        data.put("act_id", GenShinConfig.ACT_ID);
        data.put("region", GenShinConfig.REGION);
        data.put("uid", uid);
        StringEntity entity = new StringEntity(JSON.toJSONString(data), StandardCharsets.UTF_8);
        JSONObject signResult = HttpUtils.doPost(GenShinConfig.SIGN_URL, getHeadersWithDeviceId(), entity);
        if (signResult.getInteger("retcode") == 0) {
            logger.info("签到成功：{}", signResult.get("message"));
        } else {
            logger.info("签到失败：{}", signResult.get("message"));
        }
    }

    public Header[] getHeaders() {
        return getBasicHeaders().toArray(new Header[0]);
    }

    public Header[] getHeadersWithDeviceId() {
        List<BasicHeader> basicHeaders = getBasicHeaders();
        BasicHeader h1 = new BasicHeader("x-rpc-device_id", UUID.randomUUID().toString().toUpperCase());
        BasicHeader h2 = new BasicHeader("Content-Type", "application/json;charset=UTF-8");
        BasicHeader h3 = new BasicHeader("x-rpc-client_type", "5");
        BasicHeader h4 = new BasicHeader("x-rpc-app_version", GenShinConfig.APP_VERSION);
        BasicHeader h5 = new BasicHeader("DS", getDS());
        basicHeaders.add(h1);
        basicHeaders.add(h2);
        basicHeaders.add(h3);
        basicHeaders.add(h4);
        basicHeaders.add(h5);
        return basicHeaders.toArray(new Header[0]);
    }

    private List<BasicHeader> getBasicHeaders() {
        BasicHeader h1 = new BasicHeader("Cookie", cookie);
        BasicHeader h2 = new BasicHeader("User-Agent", GenShinConfig.USER_AGENT);
        BasicHeader h3 = new BasicHeader("Referer", GenShinConfig.REFERER_URL);
        BasicHeader h4 = new BasicHeader("Accept-Encoding", "gzip, deflate, br");
        BasicHeader h5 = new BasicHeader("x-rpc-channel", "appstore");
        BasicHeader h6 = new BasicHeader("accept-language", "zh-CN,zh;q=0.9,ja-JP;q=0.8,ja;q=0.7,en-US;q=0.6,en;q=0.5");
        BasicHeader h7 = new BasicHeader("accept-encoding", "gzip, deflate");
        BasicHeader h8 = new BasicHeader("x-requested-with", "com.mihoyo.hyperion");
        BasicHeader h9 = new BasicHeader("Host", "api-takumi.mihoyo.com");
        return new ArrayList<>(Arrays.asList(h1, h2, h3, h4, h5, h6, h7, h8, h9));
    }

    private String getDS() {
        String n = "h8w582wxwgqvahcdkpvdhbh2w9casgfl";
        String i = (System.currentTimeMillis() / 1000) + "";
        String r = getRandomStr();
        String c = DigestUtils.md5Hex("salt=" + n + "&t=" + i + "&r=" + r);
        return String.format("%s,%s,%s", i, r, c);
    }

    private String getRandomStr() {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 6; i++) {
            int number = random.nextInt(str.length());
            char charAt = str.charAt(number);
            sb.append(charAt);
        }
        return sb.toString();
    }
}
