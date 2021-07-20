package org.ponking.gih.sign.gs;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import org.ponking.gih.sign.gs.pojo.Award;
import org.ponking.gih.util.HttpUtils;
import org.ponking.gih.util.LoggerUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * @Author ponking
 * @Date 2021/5/7 10:10
 */
public class GenShinSignMiHoYo extends MiHoYoAbstractSign {

    private String uid;

    public GenShinSignMiHoYo(String cookie) {
        super(cookie);
        setClientType("5");
        setAppVersion("2.3.0");
        setSalt("h8w582wxwgqvahcdkpvdhbh2w9casgfl");
    }

    @Override
    public void doSign() {
        doSign(uid);
    }

    /**
     * 签到（重载doSign,主要用来本地测试）
     *
     * @param uid
     */
    public void doSign(String uid) {
        Map<String, Object> data = new HashMap<>();
        data.put("act_id", MiHoYoConfig.ACT_ID);
        data.put("region", MiHoYoConfig.REGION);
        data.put("uid", uid);
        JSONObject signResult = HttpUtils.doPost(MiHoYoConfig.SIGN_URL, getHeaders(), data);
        if (signResult.getInteger("retcode") == 0) {
            LoggerUtils.info("原神签到福利成功：{}", signResult.get("message"));
        } else {
            LoggerUtils.info("原神签到福利签到失败：{}", signResult.get("message"));
        }
    }

    public void sign() {
        LoggerUtils.info("原神福利签到开始");
        String uid = getUid();
        setUid(uid);
        isSigned();
        doSign();
        LoggerUtils.info("原神福利签到完成");
    }

    public String getUid() {
        JSONObject result = HttpUtils.doGet(MiHoYoConfig.ROLE_URL, getBasicHeaders());
        String uid = (String) result.getJSONObject("data").getJSONArray("list").getJSONObject(0).get("game_uid");
        String nickname = (String) result.getJSONObject("data").getJSONArray("list").getJSONObject(0).get("nickname");
        LoggerUtils.info("获取用户UID：{}", uid);
        LoggerUtils.info("当前用户名称：{}", nickname);
        return uid;
    }

    /**
     * 获取今天奖励详情
     *
     * @param day
     * @return
     */
    public Award getAwardInfo(int day) {
        Map<String, String> data = new HashMap<>();
        data.put("act_id", MiHoYoConfig.ACT_ID);
        data.put("region", MiHoYoConfig.REGION);
        JSONObject awardResult = HttpUtils.doGet(MiHoYoConfig.AWARD_URL, getHeaders());
        JSONArray jsonArray = awardResult.getJSONObject("data").getJSONArray("awards");
        List<Award> awards = JSON.parseObject(JSON.toJSONString(jsonArray), new TypeReference<List<Award>>() {
        });
        return awards.get(day - 1);
    }

    public boolean isSigned() {
        return isSigned(uid);
    }

    public boolean isSigned(String uid) {
        Map<String, Object> data = new HashMap<>();
        data.put("act_id", MiHoYoConfig.ACT_ID);
        data.put("region", MiHoYoConfig.REGION);
        data.put("uid", uid);
        JSONObject signInfoResult = HttpUtils.doGet(MiHoYoConfig.INFO_URL, getHeaders(), data);

        LocalDateTime time = LocalDateTime.now();
        Boolean isSign = signInfoResult.getJSONObject("data").getBoolean("is_sign");
        Integer totalSignDay = signInfoResult.getJSONObject("data").getInteger("total_sign_day");
        int day = isSign ? totalSignDay : totalSignDay + 1;
        Award award = getAwardInfo(day);

        LoggerUtils.info("{}月已签到{}天", time.getMonth().getValue(), totalSignDay);
        LoggerUtils.info("今天{}签到可获取{}{}", signInfoResult.getJSONObject("data").get("today"), award.getCnt(), award.getName());
        return isSign;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

}
