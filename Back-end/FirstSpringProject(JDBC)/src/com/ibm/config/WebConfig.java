package com.ibm.config;

import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jndi.JndiTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.ibm.dao.ChatDao;
import com.ibm.dao.InvitationDao;
import com.ibm.dao.MessageDao;
import com.ibm.dao.UserDao;
import com.ibm.dao.impl.ChatDaoImpl;
import com.ibm.dao.impl.InvitationDaoImpl;
import com.ibm.dao.impl.MessageDaoImpl;
import com.ibm.dao.impl.UserDaoImpl;

@Configuration
@ComponentScan(basePackages = { "com.ibm" })
@EnableWebMvc
public class WebConfig {
	
	private JndiTemplate jndiTemplate = new JndiTemplate();
	
	@Bean
	public ChatDao getChatDao() throws NamingException {
	    DataSource dataSource = (DataSource) jndiTemplate.lookup("java:comp/env/jdbc/chat_info");
	    return new ChatDaoImpl(dataSource);
	}
	
	@Bean
	public InvitationDao getInvitationDao() throws NamingException {
	    DataSource dataSource = (DataSource) jndiTemplate.lookup("java:comp/env/jdbc/chat_info");
	    return new InvitationDaoImpl(dataSource);
	}
	
	@Bean
	public MessageDao getMessageDao() throws NamingException {
	    DataSource dataSource = (DataSource) jndiTemplate.lookup("java:comp/env/jdbc/chat_info");
	    return new MessageDaoImpl(dataSource);
	}
	
	@Bean
	public UserDao getUserDao() throws NamingException {
		DataSource dataSource = (DataSource) jndiTemplate.lookup("java:comp/env/jdbc/chat_info");
	    return new UserDaoImpl(dataSource);
	}
	
}
