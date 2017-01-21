package bg.bgx.user;

import java.security.NoSuchAlgorithmException;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;

import bg.bgx.model.User;
import bg.bgx.security.Encrypt;

@RequestScoped
public class ResetPasswordService {
	
	@PersistenceContext(unitName = "bulgarian-grain-exchange")
	private EntityManager entityManager;
	
	@Transactional
	public void resetPassword(User user) throws NoSuchAlgorithmException{
		
		String newPassword = generateNewPassword();
		user.setPassword(Encrypt.encryptSHA256(newPassword, user.getSalt()));
		
		entityManager.merge(user);
	}

	private String generateNewPassword(){
		
	    int length = 12;
	    boolean useLetters = true;
	    boolean useNumbers = true;
	    
	    return RandomStringUtils.random(length, useLetters, useNumbers);
	}
}
