package bg.bgx.user;

import java.security.NoSuchAlgorithmException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;

import bg.bgx.email.MailService;
import bg.bgx.model.User;
import bg.bgx.security.Encrypt;

@RequestScoped
public class ResetPasswordService {
	
	@PersistenceContext(unitName = "persistenceUnit")
	private EntityManager entityManager;
	
	@Inject
	private MailService mailService;
	
	@Inject
	private transient Logger logger;
	
	@Transactional
	public void resetPassword(User user) throws NoSuchAlgorithmException{
		
		String newPassword = generateNewPassword();
		user.setPassword(Encrypt.encryptSHA256(newPassword, user.getSalt()));
		
		entityManager.merge(user);
		
		mailService.sendMail(user.getEmail(), "Resetting password", "Your new password is: " + newPassword);
		
		logger.info("Password for user: " + user.getUserName() + " has been changed.");
	}

	private String generateNewPassword(){
		
	    int length = 12;
	    boolean useLetters = true;
	    boolean useNumbers = true;
	    
	    return RandomStringUtils.random(length, useLetters, useNumbers);
	}
}
