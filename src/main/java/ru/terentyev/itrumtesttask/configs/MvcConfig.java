package ru.terentyev.itrumtesttask.configs;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import liquibase.integration.spring.SpringLiquibase;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Помимо настройки, приведенной в этом классе, возможно было бы создать, например, внешний по отношению к Docker контейнеру
 *  файл application.properties и копировать данные из него во внутренний (внутри контейнера) файл application.properties.
 *  Таким образом это позволит указывать настройки извне.
 *  Нужна следующая команда bash:
 * docker run -v /путь/к/вашему/application.properties:/путь/внутри/контейнера/application.properties your_image
 * 
 * Но поскольку в техническом задании написано по-другому, старался сделать как написано :)
 */

@EnableWebMvc
@Configuration
public class MvcConfig implements WebMvcConfigurer {
	
	private Environment environment;
	
	@Autowired
	public MvcConfig(Environment environment) {
		this.environment = environment;
	}
	
	@Bean
	public DataSource dataSource() {
	    DriverManagerDataSource dataSource = new DriverManagerDataSource();
	    
	    dataSource.setDriverClassName("org.postgresql.Driver");
	    //dataSource.setUrl(environment.getProperty("DB_URL"));
	    dataSource.setUrl(environment.getProperty("spring.datasource.url"));
	    dataSource.setUsername(environment.getProperty("spring.datasource.username"));
	    dataSource.setPassword(environment.getProperty("spring.datasource.password"));
	    return dataSource;
	}

    @Bean
    public SpringLiquibase liquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource());
        
        if (environment.getProperty("liquibase.changeLog") != null) { 
        	liquibase.setChangeLog(environment.getProperty("liquibase.changeLog"));
        } else {
        liquibase.setChangeLog("classpath:db/changelog/changelog.yaml");
        }
        return liquibase;
    }
	
	
   @Bean
   HiddenHttpMethodFilter hiddenHttpMethodFilter() {
       return new HiddenHttpMethodFilter();
   }
   
   @Bean
   public ObjectMapper objectMapper() {
       return JsonMapper.builder()
           .addModule(new JavaTimeModule())
           .configure(SerializationFeature.INDENT_OUTPUT, true)
           .build();
   }  
  
}