package bg.bgx.rest;

import java.io.File;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;
import javax.ws.rs.core.UriInfo;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import bg.bgx.context.UserContext;
import bg.bgx.email.MailService;
import bg.bgx.model.Role;
import bg.bgx.model.User;
import bg.bgx.security.Encrypt;
import bg.bgx.user.UserService;
import bg.bgx.util.LoggerProducer;

@RunWith(Arquillian.class)
public class UserResourceTest {

	@Inject
	private UserResource userResource;
	
	@Inject
	private UserService userService;

	@Inject
	private UserContext userContext;

	@Deployment
	public static Archive<?> createTestArchive() {

		File[] files = Maven.resolver().loadPomFromFile("pom.xml")
				.importRuntimeDependencies().resolve().withTransitivity()
				.asFile();

		return ShrinkWrap.create(WebArchive.class, "test.war")
				.addClass(UserResource.class).addClass(UriInfo.class)
				.addPackage(User.class.getPackage())
				.addPackage(UserService.class.getPackage())
				.addPackage(Encrypt.class.getPackage())
				.addClass(UserContext.class).addClass(LoggerProducer.class)
				.addClass(MailService.class).addAsLibraries(files)
				.addAsResource("META-INF/persistence.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Before
	public void setUp() throws NoSuchAlgorithmException {
		
		User findByEmail = userService.findByEmail("email@fake.com");
		if(findByEmail != null){
			return;
		}
		
		User user = new User();
		user.setUserName("username");
		user.setEmail("email@fake.com");
		user.setPassword("1234");
		user.setRole(Role.ADMIN.name());
		userResource.registerUser(user);
	}

	@Test
	public void testAuthorization() throws NoSuchAlgorithmException {

		Assert.assertNotNull("User context must cointain jwt token",
				userContext.getUserJWTToken());

		String authorizationHeader = (String) userResource
				.authenticateUser("email@fake.com", "1234")
				.getHeaderString("Authorization");
		Assert.assertNotNull("Authorization header should not be null",
				authorizationHeader);
	}

	@Test
	public void testLogout() {

		userResource.authenticateUser("email@fake.com", "1234")
				.getHeaderString("Authorization");
		userResource.logout();
		Assert.assertNull("User context must be null",
				userContext.getUserJWTToken());
	}
}
