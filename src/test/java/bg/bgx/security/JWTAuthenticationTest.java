package bg.bgx.security;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Response.Status;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import bg.bgx.context.UserContext;
import bg.bgx.email.MailService;
import bg.bgx.model.Role;
import bg.bgx.model.User;
import bg.bgx.rest.UserResource;
import bg.bgx.user.UserService;
import bg.bgx.util.LoggerProducer;

@RunWith(Arquillian.class)
public class JWTAuthenticationTest {

	private static final String LOGIN_URL = "http://localhost:8080/bulgarian-grain-exchange/rest/login";
	private static final String ALL_NEWS_URL = "http://localhost:8080/bulgarian-grain-exchange/rest/news/findAll";

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
				.addClass(UserResource.class)
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
		if (findByEmail != null) {
			return;
		}

		User user = new User();
		user.setUserName("username");
		user.setEmail("email@fake.com");
		user.setPassword("1234");
		user.setRole(Role.ADMIN.name());
		userResource.registerUser(user);
		userResource.logout();
	}

	@Test
	public void testAuthenticateWithJWT() throws Exception {

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost request = createLoginRequest();

		HttpResponse response = client.execute(request);
		Assert.assertEquals("Response should be with status ok ",
				response.getStatusLine().getStatusCode(),
				Status.OK.getStatusCode());
	}

	@Test
	public void testWithNoJWT() throws ClientProtocolException, IOException {
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(ALL_NEWS_URL);

		HttpResponse response = client.execute(request);
		Assert.assertEquals("Response should be with status UNAUTHORIZED ",
				response.getStatusLine().getStatusCode(),
				Status.UNAUTHORIZED.getStatusCode());
	}
	
	@Test
	public void testWithJWT() throws ClientProtocolException, IOException{
		HttpClient client = HttpClientBuilder.create().build();
		client.execute(createLoginRequest());
		
		HttpGet request = new HttpGet(ALL_NEWS_URL);
		HttpResponse response = client.execute(request);
		Assert.assertEquals("Response should be with status ok ",
				response.getStatusLine().getStatusCode(),
				Status.OK.getStatusCode());
	}
	
	@After
	public void tearDown(){
		userResource.logout();
	}

	private HttpPost createLoginRequest() throws UnsupportedEncodingException {
		HttpPost request = new HttpPost(LOGIN_URL);
		List<NameValuePair> nvps = new ArrayList<NameValuePair>();
		nvps.add(new BasicNameValuePair("email", "email@fake.com"));
		nvps.add(new BasicNameValuePair("password", "1234"));
		request.setEntity(new UrlEncodedFormEntity(nvps));

		return request;
	}

}
