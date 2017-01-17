package bg.bgx.rest;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import bg.bgx.model.Category;
import bg.bgx.model.News;
import bg.bgx.news.NewsService;

@RequestScoped
@Path("/news")
public class NewsResource {

	@Inject
	private NewsService newsService;

	@GET
	@Path("/findAll")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getNews() {

		List<News> result = newsService.findAllNews();
		return Response.ok().entity(result).build();
	}
	
	@GET
	@Path("/findByAuthor/{author}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findByAuthor(@PathParam("author") String author){
		
		List<News> result = newsService.findByAuthor(author);
		return Response.ok().entity(result).build();
	}
	
	@GET
	@Path("/findByCategory/{category}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findByCategory(@PathParam("category") Category category){
		
		List<News> result = newsService.findByCategory(category);
		return Response.ok().entity(result).build();
	}
	
	@GET
	@Path("/findByTitle/{title}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findByTitle(@PathParam("title") String title){
		
		List<News> result = newsService.findByTitle(title);
		return Response.ok().entity(result).build();
	}
}
