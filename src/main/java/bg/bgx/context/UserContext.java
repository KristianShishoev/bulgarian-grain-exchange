package bg.bgx.context;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;

@SessionScoped
public class UserContext implements Serializable {
	
	private static final long serialVersionUID = -3886382138583590969L;
	private String userJWTToken;
	
	public String getUserJWTToken() {
		return userJWTToken;
	}
	public void setUserJWTToken(String userJWTToken) {
		this.userJWTToken = userJWTToken;
	}
}
