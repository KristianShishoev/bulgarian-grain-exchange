package bg.bgx.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name="user")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "findByUserName", query = "SELECT u FROM User u WHERE u.userName = :userName"),
    @NamedQuery(name = "findByEmail", query = "SELECT u FROM User u WHERE u.email = :email")})
public class User extends AbstractEntity{
	
	@NotNull
	@Column(unique=true)
	@Size(min=5, max=64)
	private String userName;
	
	@NotNull
	@Column(unique=true)
	@Size(min=1, max=128)
	private String email;
	
	@NotNull
	@Size(min=8, max=64)
	private String password;

	@NotNull
	private byte[] salt;
	
	public byte[] getSalt() {
		return salt;
	}

	public void setSalt(byte[] salt) {
		this.salt = salt;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
