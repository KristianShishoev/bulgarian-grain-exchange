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
@Security
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {
	
	@Inject
	private UserContext userContext;
	
	 @Override
	    public void filter(ContainerRequestContext requestContext) throws IOException {

		 	String loggedUserJWT = userContext.getUserJWTToken();
		 
	        if (loggedUserJWT == null) {
	            throw new NotAuthorizedException("Authorization header must be provided");
	        }

	        try {
	        	Key key = KeyGenerator.generateKey();
	            Jwts.parser().setSigningKey(key).parseClaimsJws(loggedUserJWT);
	        } catch (Exception e) {
	            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
	        }
	    }
}
