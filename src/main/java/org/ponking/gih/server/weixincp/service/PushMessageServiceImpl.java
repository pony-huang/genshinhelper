package org.ponking.gih.server.weixincp.service;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.ponking.gih.server.weixincp.param.AppPushMarkdownParam;
import org.ponking.gih.server.weixincp.param.AppPushTextCardParam;
import org.ponking.gih.server.weixincp.param.AppPushTextParam;
import org.ponking.gih.server.weixincp.result.AccessTokenResult;
import org.ponking.gih.server.weixincp.result.Result;
import org.ponking.gih.util.HttpUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author ponking
 * @Date 2021/5/4 13:26
 */

public class PushMessageServiceImpl implements PushMessageService {

    private final String BASE_URL = "https://qyapi.weixin.qq.com/cgi-bin/message/send";

    private final String TOKEN_URL = "https://qyapi.weixin.qq.com/cgi-bin/gettoken";

    private final WXUserInfo userInfo;

    public PushMessageServiceImpl(WXUserInfo userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public Result sendWithText(String text) {
        try {
            List<NameValuePair> params = getBasicParams();
            params.add(new BasicNameValuePair("text", text));
            URI uri = new URIBuilder(BASE_URL)
                    .setParameters(params).build();
            AppPushTextParam param = AppPushTextParam.build(text, userInfo.getAgentId(), userInfo.getToUser());
            StringEntity entity = new StringEntity(JSON.toJSONString(param), StandardCharsets.UTF_8);
            HttpEntity httpEntity = HttpUtils.doPost(uri, entity);
            assert httpEntity != null;
            String result = EntityUtils.toString(httpEntity);
            return JSON.parseObject(result, Result.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Result sendWithTextCard(CardMessage message) {
        try {
            List<NameValuePair> params = getBasicParams();
            URI uri = new URIBuilder(BASE_URL)
                    .setParameters(params).build();
            String btntxt = message.getBtntxt() == null ? "更多" : message.getBtntxt();
            AppPushTextCardParam param = AppPushTextCardParam.build(userInfo.getAgentId(), message.getDescription(), btntxt,
                    message.getTitle(), message.getUrl(), userInfo.getToUser());
            StringEntity entity = new StringEntity(JSON.toJSONString(param), StandardCharsets.UTF_8);
            HttpEntity httpEntity = HttpUtils.doPost(uri, entity);
            assert httpEntity != null;
            String result = EntityUtils.toString(httpEntity);
            return JSON.parseObject(result, Result.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Result sendWithMarkdown(String content) {
        try {
            List<NameValuePair> params = getBasicParams();
            params.add(new BasicNameValuePair("markdown", content));
            URI uri = new URIBuilder(BASE_URL)
                    .setParameters(params).build();
            AppPushMarkdownParam param = AppPushMarkdownParam.build(content, userInfo.getAgentId(), userInfo.getToUser());
            StringEntity entity = new StringEntity(JSON.toJSONString(param), StandardCharsets.UTF_8);
            HttpEntity httpEntity = HttpUtils.doPost(uri, entity);
            assert httpEntity != null;
            String result = EntityUtils.toString(httpEntity);
            return JSON.parseObject(result, Result.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void sendWithTextCard(String title, String desp) {
        try {
            List<NameValuePair> params = getBasicParams();
            URI uri = new URIBuilder(BASE_URL)
                    .setParameters(params).build();
            AppPushTextCardParam param = AppPushTextCardParam.build(userInfo.getAgentId(), desp, "更多",
                    title, "https://github.com/PonKing66/genshi-helper", userInfo.getToUser());
            StringEntity entity = new StringEntity(JSON.toJSONString(param), StandardCharsets.UTF_8);
            HttpEntity httpEntity = HttpUtils.doPost(uri, entity);
            assert httpEntity != null;
            String result = EntityUtils.toString(httpEntity);
            JSON.parseObject(result, Result.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public String getToken() {
        AccessTokenResult result = null;
        try {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("corpid", userInfo.getCorpId()));
            params.add(new BasicNameValuePair("corpsecret", userInfo.getCorpSecret()));
            URI uri = new URIBuilder(TOKEN_URL)
                    .setParameters(params).build();
            HttpEntity httpEntity = HttpUtils.doGetDefault(uri);
            assert httpEntity != null;
            result = JSON.parseObject(EntityUtils.toString(httpEntity), AccessTokenResult.class);
        } catch (URISyntaxException | IOException e1) {
            e1.printStackTrace();
        }
        if (result == null) {
            return null;
        }
        return result.getAccessToken();
    }


    private List<NameValuePair> getBasicParams() {
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("access_token", getToken()));
        return params;
    }
}
