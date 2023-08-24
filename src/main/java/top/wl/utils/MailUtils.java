package top.wl.utils;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailUtils {

  public static void sendMail(String email, String emailMsg)
      throws AddressException, MessagingException {
    /**
     * 需要发件邮箱授权码，选择忽略。
     */
//    Properties props = new Properties();
//    props.setProperty("mail.transport.protocol", "SMTP");
//    props.setProperty("mail.host", "smtp.163.com");
//    props.setProperty("mail.smtp.auth", "true");// 指定验证为true
//
//    Authenticator auth = new Authenticator() {
//      @Override
//      public PasswordAuthentication getPasswordAuthentication() {
//        return new PasswordAuthentication("wenlin_uestc@163.com", "授权码");
//      }
//    };
//
//    Session session = Session.getInstance(props, auth);
//    Message message = new MimeMessage(session);
//    message.setFrom(new InternetAddress("wenlin_uestc@163.com"));
//    message.setRecipient(RecipientType.TO, new InternetAddress(email));
//    message.setSubject("用户激活");
//    message.setContent(emailMsg, "text/html;charset=utf-8");
//    Transport.send(message);
  }
}
