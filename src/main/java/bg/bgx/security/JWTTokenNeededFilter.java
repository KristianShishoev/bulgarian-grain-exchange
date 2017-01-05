package bg.bgx.security;

import java.io.IOException;
import java.security.Key;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import bg.bgx.context.UserContext;

import io.jsonwebtoken.Jwts;

@Provider
@JWTTokenNeeded
@Priority(Priorities.AUTHENTICATION)
public class JWTTokenNeededFilter implements ContainerRequestFilter {
	
	@Inject
	private UserContext userContext;
	
	 @Override
	    public void filter(ContainerRequestContext requestContext) throws IOException {

		 	String loggedUserJWT = userContext.getUserJWTToken();
		 
	        if (loggedUserJWT == null || !loggedUserJWT.startsWith("Bearer ")) {
	            throw new NotAuthorizedException("Authorization header must be provided");
	        }

	        String token = loggedUserJWT.substring("Bearer".length()).trim();

	        try {
	        	Key key = KeyGenerator.generateKey();
	            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
	        } catch (Exception e) {
	            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
	        }
	    }
}
