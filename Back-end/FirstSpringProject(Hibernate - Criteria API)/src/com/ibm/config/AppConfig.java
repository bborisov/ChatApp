package com.ibm.config;

import java.util.Properties;

import javax.naming.NamingException;
import javax.sql.DataSource;
import org.hibernate.SessionFactory;
import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.ibm.dao.ChatDao;
import com.ibm.dao.InvitationDao;
import com.ibm.dao.MessageDao;
import com.ibm.dao.UserDao;
import com.ibm.dao.impl.ChatDaoImpl;
import com.ibm.dao.impl.InvitationDaoImpl;
import com.ibm.dao.impl.MessageDaoImpl;
import com.ibm.dao.impl.UserDaoImpl;
 
@Configuration
@EnableTransactionManagement
@PropertySource({ "classpath:persistence-mysql.properties" })
@ComponentScan(basePackages = { "com.ibm" })
public class AppConfig {
 
   @Autowired
   private Environment env;
 
   @Bean
   public LocalSessionFactoryBean sessionFactory() {
      LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
      sessionFactory.setDataSource(restDataSource());
      sessionFactory.setPackagesToScan(new String[] { "org.baeldung.spring.persistence.model", "com.ibm.entities" } );
      sessionFactory.setHibernateProperties(hibernateProperties());
 
      return sessionFactory;
   }
 
   @Bean
   public DataSource restDataSource() {
      BasicDataSource dataSource = new BasicDataSource();
      dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
      dataSource.setUrl(env.getProperty("jdbc.url"));
      dataSource.setUsername(env.getProperty("jdbc.user"));
      dataSource.setPassword(env.getProperty("jdbc.pass"));
 
      return dataSource;
   }
 
   @Bean
   @Autowired
   public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
      HibernateTransactionManager txManager = new HibernateTransactionManager();
      txManager.setSessionFactory(sessionFactory);
 
      return txManager;
   }
 
   @Bean
   public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
      return new PersistenceExceptionTranslationPostProcessor();
   }
 
   @SuppressWarnings("serial")
   Properties hibernateProperties() {
      return new Properties() {
         {
            setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
            setProperty("hibernate.show_sql", "true");
            setProperty("hibernate.format_sql", "true");
         }
      };
   }
   
   @Bean
   public ChatDao getChatDao() throws NamingException {
	   return new ChatDaoImpl();
   }
   
   @Bean
   public InvitationDao getInvitationDao() throws NamingException {
	  return new InvitationDaoImpl();
   }
	
   @Bean
   
   public MessageDao getMessageDao() throws NamingException {
	  return new MessageDaoImpl();
   }
	
   @Bean
   public UserDao getUserDao() throws NamingException {
	  return new UserDaoImpl();
   }
   
}

//package com.ibm.config;
//
//import javax.naming.NamingException;
//import javax.sql.DataSource;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jndi.JndiTemplate;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//
//import com.ibm.dao.ChatDao;
//import com.ibm.dao.InvitationDao;
//import com.ibm.dao.MessageDao;
//import com.ibm.dao.UserDao;
//import com.ibm.dao.impl.ChatDaoImpl;
//import com.ibm.dao.impl.InvitationDaoImpl;
//import com.ibm.dao.impl.MessageDaoImpl;
//import com.ibm.dao.impl.UserDaoImpl;
//
//@Configuration
//@ComponentScan(basePackages = { "com.ibm" })
//@EnableWebMvc
//public class BeansConfig {
//	
//	private JndiTemplate jndiTemplate = new JndiTemplate();
//	
//	@Bean
//	public ChatDao getChatDao() throws NamingException {
//	    DataSource dataSource = (DataSource) jndiTemplate.lookup("java:comp/env/jdbc/chat_info");
//	    return new ChatDaoImpl(dataSource);
//	}
//	
//	@Bean
//	public InvitationDao getInvitationDao() throws NamingException {
//	    DataSource dataSource = (DataSource) jndiTemplate.lookup("java:comp/env/jdbc/chat_info");
//	    return new InvitationDaoImpl(dataSource);
//	}
//	
//	@Bean
//	public MessageDao getMessageDao() throws NamingException {
//	    DataSource dataSource = (DataSource) jndiTemplate.lookup("java:comp/env/jdbc/chat_info");
//	    return new MessageDaoImpl(dataSource);
//	}
//	
//	@Bean
//	public UserDao getUserDao() throws NamingException {
//		DataSource dataSource = (DataSource) jndiTemplate.lookup("java:comp/env/jdbc/chat_info");
//	    return new UserDaoImpl(dataSource);
//	}
//	
//}