package bg.bgx.rest;

import java.io.File;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.core.Response.Status;

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

import bg.bgx.model.Category;
import bg.bgx.model.News;
import bg.bgx.model.User;
import bg.bgx.news.NewsService;
import bg.bgx.security.Encrypt;
import bg.bgx.user.UserService;
import bg.bgx.util.LoggerProducer;

@RunWith(Arquillian.class)
public class AdminResourceTest {

	@Inject
	private AdminResource adminResource;

	@Deployment
	public static Archive<?> createTestArchive() {

		File[] files = Maven.resolver().loadPomFromFile("pom.xml")
				.importRuntimeDependencies().resolve().withTransitivity()
				.asFile();

		return ShrinkWrap.create(WebArchive.class, "test.war")
				.addClass(UserService.class).addClass(NewsService.class)
				.addClass(LoggerProducer.class).addClass(AdminResource.class)
				.addPackage(User.class.getPackage())
				.addPackage(Encrypt.class.getPackage()).addAsLibraries(files)
				.addAsResource("META-INF/persistence.xml")
				.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
	}

	@Before
	public void setUp() {

		News news = new News();
		news.setAuthor("author");
		news.setCategory(Category.GRAIN);
		news.setContent("testcontent");
		news.setTitle("testTitle");
		adminResource.insertNews(news);

		news = new News();
		news.setTitle("otherTitle");
		news.setContent("moreContent");
		news.setCategory(Category.PRICES);
		adminResource.insertNews(news);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testFindAllNews() throws NoSuchAlgorithmException {

		List<News> allNews = (List<News>) adminResource.findAllNews()
				.getEntity();
		Assert.assertEquals("Response from call must be with status '200'",
				adminResource.findAllNews().getStatus(),
				Status.OK.getStatusCode());
		Assert.assertEquals("Size of all news must be 2", allNews.size(), 2);
	}

	@Test
	public void testFindByTitleNews() {

		News entity = (News) adminResource.findNews("testTitle").getEntity();
		Assert.assertEquals("Title of the news must be 'testTitle'",
				"testTitle", entity.getTitle());
		Assert.assertEquals("Content of news must be 'testcontent'",
				"testcontent", entity.getContent());

	}

	@Test
	public void testUpdateNews() {

		News news = (News) adminResource.findNews("testTitle").getEntity();
		news.setContent("updatedContent");
		adminResource.updateNews(news);
		News updatedNews = (News) adminResource.findNews("testTitle")
				.getEntity();
		Assert.assertEquals(
				"News content after update must be 'updatedContent'", "updatedContent", updatedNews.getContent());

	}

	@Test
	@SuppressWarnings("unchecked")
	public void testRemoveNews(){
		List<News> entities = (List<News>) adminResource.findAllNews().getEntity();
		Assert.assertEquals(2, entities.size());
		adminResource.removeNews("testTitle");
		List<News> removedNews = (List<News>) adminResource.findAllNews().getEntity();
		Assert.assertEquals("Only one news must exist", 1, removedNews.size());
	}
	
	@After
	public void tearDown() {
		adminResource.removeNews("testTitle");
		adminResource.removeNews("otherTitle");
	}
}
