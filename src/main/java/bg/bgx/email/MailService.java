package bg.bgx.email;

import javax.annotation.Resource;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

@RequestScoped
public class MailService {
	
    @Resource(name = "java:jboss/mail/bgx")
    private Session session;
    
	@Inject
	private transient Logger logger;
    
	public void sendMail(String to, String subject, String text) {

		try {
			Message message = new MimeMessage(session);
			message.setRecipients(Message.RecipientType.TO,
					InternetAddress.parse(to));
			message.setSubject(subject);
			message.setText(text);

			Transport.send(message);
			
			logger.info("Successfully sended email to: " + to + " with subject: " + subject);
		} catch (MessagingException e) {
			
			logger.error("Error while sending email to: " + to + " with subject: " + subject);
			throw new RuntimeException(e);
		}
	}
	
}
	
