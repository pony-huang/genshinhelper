package org.ponking.gih.push;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.ponking.gih.util.MailUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * @author PonKing
 * @description TODO
 * @date 8/15/2022
 */
public class MailMessagePush implements MessagePush {

    private final String from;

    private final String to;

    public MailMessagePush(String from, String to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void sendMessage(String text, String deps) {
        Session session = MailUtil.getSession();
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            msg.setSubject(text);
            MailUtil.collect(new BufferedReader(new StringReader(deps)),msg);
            MailUtil.send(msg);
        } catch (MessagingException | IOException e) {
//            throw new RuntimeException(e);
            e.printStackTrace();
        }
    }

}