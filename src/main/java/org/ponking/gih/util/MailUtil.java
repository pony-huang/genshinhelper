package org.ponking.gih.util;

import jakarta.activation.DataHandler;
import jakarta.mail.*;
import jakarta.mail.util.ByteArrayDataSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Properties;

/**
 * @author PonKing
 * @description TODO
 * @date 8/15/2022
 */
public class MailUtil {

    private static volatile Session session = null;

    private MailUtil() {
    }

    public static Session getSession() {
        if (session == null) {
            throw new RuntimeException("Session not init!!!");
        }
        return session;
    }

    public static void init(String username, String password, int port, String host) {
        if (session == null) {
            synchronized (MailUtil.class) {
                if (session == null) {
                    Properties props = new Properties();
                    props.put("mail.smtp.ssl", true);
//                    props.put("mail.debug", "true");
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.starttls.enable", "true");
                    props.put("mail.smtp.host", host);
                    props.put("mail.smtp.port", port);
                    props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                    //create the Session object
                    session = Session.getInstance(props, new Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(username, password);
                        }
                    });
                }
            }
        }
    }

    public static void send(Message message) throws MessagingException {
        Transport.send(message);
    }

    public static void collect(BufferedReader in, Message msg) throws MessagingException, IOException {
        String line;
        String subject = msg.getSubject();
        StringBuilder sb = new StringBuilder();
        sb.append("<HTML>\n");
        sb.append("<HEAD>\n");
        sb.append("<TITLE>\n");
        sb.append(subject).append("\n");
        sb.append("</TITLE>\n");
        sb.append("</HEAD>\n");

        sb.append("<BODY>\n");
        sb.append("<H1>").append(subject).append("</H1>").append("\n");

        while ((line = in.readLine()) != null) {
            sb.append("<p>").append(line).append("</p>");
            sb.append("\n");
        }

        sb.append("</BODY>\n");
        sb.append("</HTML>\n");

        msg.setDataHandler(new DataHandler(
                new ByteArrayDataSource(sb.toString(), "text/html")));
    }

}
