package org.ponking.gih.sign;

import org.ponking.gih.push.MessagePush;

/**
 * @author PonKing
 * @description TODO
 * @date 8/13/2022
 */
public class MessageTask {

    public MessagePush messagePush;

    private String fileName;


    public MessageTask(MessagePush messagePush, String fileName) {
        this.messagePush = messagePush;
        this.fileName = fileName;
    }

    public MessagePush getMessagePush() {
        return messagePush;
    }

    public void setMessagePush(MessagePush messagePush) {
        this.messagePush = messagePush;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}