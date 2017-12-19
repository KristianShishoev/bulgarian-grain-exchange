package bg.bgx.news;

import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import bg.bgx.model.Category;
import bg.bgx.model.News;

@RequestScoped
public class NewsService {
	
	@PersistenceContext(unitName = "persistenceUnit")
	private EntityManager entityManager;
	
	@Transactional
	public News insertNews(News news) {
		entityManager.persist(news);
		return news;
	}
	
	@Transactional
	public List<News> findByAuthor(String author) {

		TypedQuery<News> query = entityManager
				.createNamedQuery("findByAuthor", News.class)
				.setParameter("author", author);

		return query.getResultList();
	}
	
	@Transactional
	public List<News> findByTitle(String phrase) {

		TypedQuery<News> query = entityManager
				.createNamedQuery("findByTitle", News.class)
				.setParameter("phrase", "%" + phrase + "%");

		return query.getResultList();
	}
	
	@Transactional
	public List<News> findByCategory(Category category) {

		TypedQuery<News> query = entityManager
				.createNamedQuery("findByCategory", News.class)
				.setParameter("category", category);

		return query.getResultList();
	}

	@Transactional
	public List<News> findAllNews() {

		TypedQuery<News> query = entityManager
				.createNamedQuery("findAll", News.class);

		return query.getResultList();
	}
	
	@Transactional
	public News findNews(String title){
		
		TypedQuery<News> query = entityManager.createNamedQuery("findNews", News.class).setParameter("title", title);
		return query.getResultList().get(0);
	}
	
	@Transactional
	public void updateNews(News news){
		
		entityManager.merge(news);
	}
	
	@Transactional
	public void removeNews(News news){
		
		if(entityManager.contains(news)){
			entityManager.remove(news);
			return;
		}
		
		entityManager.remove(entityManager.merge(news));
	}
}
