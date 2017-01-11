package bg.bgx.rest;

import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import bg.bgx.context.UserContext;
import bg.bgx.model.Role;
import bg.bgx.model.User;
import bg.bgx.security.Encrypt;
import bg.bgx.security.KeyGenerator;
import bg.bgx.security.Security;
import bg.bgx.user.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@RequestScoped
@Path("/")
public class UserResource {

	@Inject
	private UserService userDao;

	@Inject
	private UserContext userContext;

	@Context
	private UriInfo uriInfo;

	// test purposes
	@GET
	@Security({Role.ADMIN})
	@Path("/sec")
	public String getSec() {
		return "Security";
	}

	// test purposes
	@POST
	@Path("/log")
	public Response authenticateUser() {

		try {
			User user = authenticate("email", "password");
			String token = getToken("email", "ADMIN");
			userContext.setUserJWTToken(token);

			return Response.ok().build();
		} catch (Exception e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response authenticateUser(@FormParam("email") String email,
			@FormParam("password") String password) {

		try {
			User user = authenticate(email, password);
			String token = getToken(email, user.getRole());
			userContext.setUserJWTToken(token);

			return Response.ok().build();
		} catch (Exception e) {
			return Response.status(Response.Status.UNAUTHORIZED).build();
		}
	}

	@POST
	@Path("/logout")
	public Response logout() {

		userContext.setUserJWTToken(null);
		return Response.ok().build();
	}

	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response registerUser(User user) throws NoSuchAlgorithmException {

		byte[] salt = Encrypt.getSalt();
		user.setPassword(Encrypt.encryptSHA256(user.getPassword(), salt));
		user.setSalt(salt);
		userDao.addUser(user);

		String token = getToken(user.getUserName(), user.getRole());
		userContext.setUserJWTToken(token);

		return Response.status(Response.Status.OK).build();
	}

	private String getToken(String login, String role) {

		Map<String, Object> claims = new HashMap<>();
		claims.put("role", role);
		claims.put("user", login);

		Key key = KeyGenerator.generateKey();
		String jwtToken = Jwts.builder()
				.setIssuer(uriInfo.getAbsolutePath().toString())
				.setIssuedAt(new Date()).setClaims(claims)
				.signWith(SignatureAlgorithm.HS512, key).compact();
		return jwtToken;
	}

	private User authenticate(String login, String password) {

		User findedUser = userDao.findByUserName(login);

		if (findedUser == null) {
			throw new SecurityException("Invalid user/password");
		}

		String pass = findedUser.getPassword();
		byte[] salt = findedUser.getSalt();

		if (!pass.equals(Encrypt.encryptSHA256(password, salt))) {
			throw new SecurityException("Invalid user/password");
		}

		return findedUser;
	}
}
