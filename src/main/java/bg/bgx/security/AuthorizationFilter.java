package bg.bgx.security;

import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import bg.bgx.context.UserContext;
import bg.bgx.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Provider
@Security
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {

	@Inject
	private UserContext userContext;

	@Context
	private ResourceInfo resourceInfo;

	@Override
	public void filter(ContainerRequestContext requestContext)
			throws IOException {

		Class<?> resourceClass = resourceInfo.getResourceClass();
		List<Role> classRoles = extractRoles(resourceClass);
		Method resourceMethod = resourceInfo.getResourceMethod();
		List<Role> methodRoles = extractRoles(resourceMethod);
		
		try {

			if (methodRoles.isEmpty()) {
				checkPermissions(classRoles);
			} else {
				checkPermissions(methodRoles);
			}

		} catch (Exception e) {

			requestContext.abortWith(
					Response.status(Response.Status.FORBIDDEN).build());
		}
	}

	private List<Role> extractRoles(AnnotatedElement annotatedElement) {

		if (annotatedElement == null) {
			return new ArrayList<Role>();
		}

		Security secured = annotatedElement
				.getAnnotation(Security.class);

		if (secured == null) {
			return new ArrayList<Role>();
		} else {
			
			Role[] allowedRoles = secured.value();
			return Arrays.asList(allowedRoles);
		}
	}

	private void checkPermissions(List<Role> allowedRoles) {

		if (allowedRoles.isEmpty()) {
			return;
		}

		String loggedUserJWT = userContext.getUserJWTToken();
		Claims claims = Jwts.parser().setSigningKey(KeyGenerator.generateKey())
				.parseClaimsJws(loggedUserJWT).getBody();
		String userRole =  (String) claims.get("role");

		for(Role role: allowedRoles){
			if(role.toString().equals(userRole)){
				return;
			}
		}
		
		throw new RuntimeException("forbidden");
	}
}
