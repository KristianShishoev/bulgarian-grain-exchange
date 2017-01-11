package bg.bgx.model;

import java.util.Map;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name="news")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "findByAuthor", query = "SELECT n FROM News n WHERE n.author = :author"),
    @NamedQuery(name = "findByTitle", query = "SELECT n From News n WHERE n.title LIKE :phrase"),
    @NamedQuery(name = "findByCategory", query = "Select n From News n WHERE n.category = :category")
})
public class News extends AbstractEntity{
	
	@NotNull
	private String title;
	
	@NotNull
	private Category category;
	
	@ManyToOne
	private User author;
	
	private int views;
	
	@NotNull
	private String content;
	
	@ElementCollection
	private Map<String, String> images;
	
	public News(){
		
	}
	
	public News(String title, Category category, User author, int views,
			String content, Map<String, String> images) {
		super();
		this.title = title;
		this.category = category;
		this.author = author;
		this.views = views;
		this.content = content;
		this.images = images;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public int getViews() {
		return views;
	}

	public void setViews(int views) {
		this.views = views;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Map<String, String> getImages() {
		return images;
	}

	public void setImages(Map <String, String> images) {
		this.images = images;
	}
}
