package org.ponking.gih.server.config;

import org.ponking.gih.server.result.Result;

/**
 * @Author ponking
 * @Date 2021/5/4 13:24
 */
public interface PushMessageService {

    /**
     * 纯文本样式
     * @param text
     */
    Result sendWithText(String text);


    /**
     * 卡片消息样式
     * @param message
     */
    Result sendWithTextCard(CardMessage message);


    /**
     * 获取access_token
     * @return
     */
    String getToken();
}
