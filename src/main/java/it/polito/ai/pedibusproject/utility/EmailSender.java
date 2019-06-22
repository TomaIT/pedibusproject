package it.polito.ai.pedibusproject.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class EmailSender {
    private JavaMailSender javaMailSender;
    private static final Logger LOG = LoggerFactory.getLogger(EmailSender.class);
    @Value("${spring.mail.username}")
    private String systemEmail;

    @Autowired
    public EmailSender(JavaMailSender javaMailSender){
        this.javaMailSender=javaMailSender;
    }

    @Async("threadPoolTaskExecutor")
    public void sendEmail(SimpleMailMessage simpleMailMessage){
        try {
            this.javaMailSender.send(simpleMailMessage);
        }catch (Exception e){
            LOG.error("sendEmail <"+simpleMailMessage.toString()+">\n"+e.getMessage());
        }
    }

    @Async("threadPoolTaskExecutor")
    public void sendEmail(String subject,String body,String emailTo){
        SimpleMailMessage temp = new SimpleMailMessage();
        temp.setFrom(systemEmail);
        temp.setTo(emailTo);
        temp.setSubject(subject);
        temp.setText(body);
        sendEmail(temp);
    }
}
