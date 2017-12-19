package bg.bgx.user;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import bg.bgx.model.News;
import bg.bgx.model.User;

@RequestScoped
public class UserService {

	@PersistenceContext(unitName = "persistenceUnit")
	private EntityManager entityManager;

	@Transactional
	public User addUser(User user) {
		entityManager.persist(user);
		return user;
	}

	@Transactional
	public User findByEmail(String email) {

		TypedQuery<User> query = entityManager
				.createNamedQuery("findByEmail", User.class)
				.setParameter("email", email);

		User result = query.getResultList().size() > 0 ? query.getResultList().get(0)
				: null;

		return result;
	}
	
	@Transactional
	public List<User> findAllUsers() {

		TypedQuery<User> query = entityManager
				.createNamedQuery("findAllUsers", User.class);
		return query.getResultList();
	}
}
