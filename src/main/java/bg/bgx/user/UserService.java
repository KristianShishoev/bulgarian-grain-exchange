package bg.bgx.user;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import bg.bgx.model.User;

@RequestScoped
public class UserService {

	@PersistenceContext(unitName = "bulgarian-grain-exchange")
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
}
