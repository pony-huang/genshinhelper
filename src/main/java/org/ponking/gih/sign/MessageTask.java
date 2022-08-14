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

    private boolean push;


    public MessageTask(MessagePush messagePush, String fileName) {
        this.messagePush = messagePush;
        this.fileName = fileName;
        this.push = false;
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

    public boolean isPush() {
        return push;
    }

    public void setPush(boolean push) {
        this.push = push;
    }
}