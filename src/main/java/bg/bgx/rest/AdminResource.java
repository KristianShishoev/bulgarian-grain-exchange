package bg.bgx.rest;

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

import bg.bgx.model.News;
import bg.bgx.news.NewsService;

@Path("/admin")
@RequestScoped
//@Security({Role.ADMIN})
public class AdminResource {
	
	@Inject
	private NewsService newsService;
	
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
			return Response.status(Status.BAD_REQUEST).build();
		}
		
		newsService.removeNews(news);
		return Response.status(Status.NO_CONTENT).build();
	}

}
