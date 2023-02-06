package kr.go.museum.dino.smartapp.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.persistence.Persistence;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.web.context.ConfigurableWebApplicationContext;

public class PropertyInitializer implements ApplicationContextInitializer<ConfigurableWebApplicationContext> {
	
	private Environment env;
	
	public static Map prop;
	
	public void initialize(ConfigurableWebApplicationContext ctx) {
		String fileStr = "config/application-"+System.getProperty("spring.profiles.active")+".properties";
		PropertySource propertySource = null;
		try {
			propertySource = new ResourcePropertySource(new ClassPathResource(fileStr));
		} catch (IOException e) {
			e.printStackTrace();
		}
		ctx.getEnvironment().getPropertySources().addFirst(propertySource);

		ConfigurableEnvironment env = ctx.getEnvironment(); // Environment객체 가져옴
		prop = new HashMap();
		prop.put("javax.persistence.jdbc.driver", env.getProperty("db.driver"));
		prop.put("javax.persistence.jdbc.user", env.getProperty("db.user"));
		prop.put("javax.persistence.jdbc.password", env.getProperty("db.password"));
		prop.put("javax.persistence.jdbc.url", env.getProperty("db.url"));
		
		prop.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
		prop.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
		prop.put("hibernate.format_sql", env.getProperty("hibernate.format_sql"));
		prop.put("hibernate.use_sql_comments", env.getProperty("hibernate.use_sql_comments"));
		prop.put("hibernate.id.new_generator_mappings", env.getProperty("hibernate.id.new_generator_mappings"));
	}
}