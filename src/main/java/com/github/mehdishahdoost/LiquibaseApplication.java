package com.github.mehdishahdoost;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.sql.init.dependency.DependsOnDatabaseInitialization;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@SpringBootApplication
public class LiquibaseApplication {

	public static void main(String[] args) {
		SpringApplication.run(LiquibaseApplication.class, args);
	}



	@Bean
	@DependsOnDatabaseInitialization
	ApplicationRunner runner(JdbcTemplate jdbcTemplate) {
		return args -> {
			String sql = """
						select c.id as comment_id,
						a.title as title,
						a.id as id
						from articles a
						left join comments c
						on a.id = c.article_id;
					""";
			List<Article> articles = jdbcTemplate.query(sql, new ArticleRowMapper());
			new HashSet<>(articles).forEach(System.out::println);
		};
	}


}

record Article(Long id, String title, String author, List<Comments> comments){}

record Comments(Long id, String comment){}

class ArticleRowMapper implements RowMapper<Article> {

	private Map<Long, Article> articles = new ConcurrentHashMap<>();

	@Override
	public Article mapRow(ResultSet rs, int rowNum) throws SQLException {

		var title = rs.getString("title");
		var id = rs.getLong("id");
//		articles.put(id, );
		return new Article(id, title, "", null);
	}
}

