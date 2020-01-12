package stefanowicz.kacper.help;

import stefanowicz.kacper.exception.AppException;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {
    private static final String EMAIL_ADDRESS = "test.kacper98@gmail.com";
    private static final String EMAIL_PASSWORD = "testkacper123";


    public boolean send(String to, String title, String contentAsHtml){
        try{
            System.out.println("Sending email...");
            Session session = createSession();
            MimeMessage mimeMessage = new MimeMessage(session);
            prepareEmailMessage(mimeMessage, to, title, contentAsHtml);
            Transport.send(mimeMessage);
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
            throw new AppException("Email send exception");
        }
    }

    private void prepareEmailMessage(MimeMessage mimeMessage, String to, String title, String contentAsHtml){
        try{
            mimeMessage.setContent(contentAsHtml, "text/html; charset=utf-8");
            mimeMessage.setFrom(new InternetAddress(EMAIL_ADDRESS));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            mimeMessage.setSubject(title);
        }
        catch (Exception e){
            throw new AppException("Prepare emial message exception");
        }
    }


    private Session createSession(){
        Properties properties = new Properties();

        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.auth", "true");

        return Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_ADDRESS, EMAIL_PASSWORD);
            }
        });
    }
}
