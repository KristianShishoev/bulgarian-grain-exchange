package bg.bgx.rest;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.log4j.Logger;

import bg.bgx.model.News;
import bg.bgx.model.User;
import bg.bgx.news.NewsService;
import bg.bgx.user.UserService;

@Path("/admin")
@RequestScoped
//@Security({Role.ADMIN})
public class AdminResource {
	
	@Inject
	private NewsService newsService;
	
	@Inject
	private UserService userService;
	
	@Inject
	private transient Logger logger;
	
	@POST
	@Path("/insertNews")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response insertNews(News news) {
		
		newsService.insertNews(news);
		return Response.ok().build();
	}
	
	@GET
	@Path("findNews/{title}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findNews(@PathParam("title") String title){
		
		News result = newsService.findNews(title);
		return Response.ok().entity(result).build();
	}
	
	@PUT
	@Path("/updateNews")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response updateNews(News news){
		
		newsService.updateNews(news);
		return Response.ok().build();
	}
	
	@DELETE
	@Path("/removeNews/{title}")
	public Response removeNews(@PathParam("title") String title){
		
		News news = newsService.findNews(title);
		if(news == null){
			logger.error("Cannot find news with title: " + title);
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		newsService.removeNews(news);
		logger.info("News with title: " + title + " have been removed.");
		return Response.status(Status.NO_CONTENT).build();
	}
	
	@GET
	@Path("/findAllUsers")
	@Produces(MediaType.APPLICATION_JSON)
	public Response findAllUsers(){
		List<User> users = userService.findAllUsers();
		return Response.ok().entity(users).build();
	}
}
