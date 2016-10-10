package com.ibm.dao.impl;

import java.sql.Timestamp;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import com.ibm.dao.MessageDao;
import com.ibm.entities.MessageEntity;
import com.ibm.other.Message;

@SuppressWarnings("unchecked")
@Transactional
public class MessageDaoImpl implements MessageDao {

	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	public Message sendMessage(int senderId, int chatId, String content) {
		
		Timestamp tms = new Timestamp(System.currentTimeMillis());
		MessageEntity messageEntity = new MessageEntity(content, tms, tms, chatId, senderId);
			
		Session session = sessionFactory.getCurrentSession();
		session.persist(messageEntity);
		
		return (Message) messageEntity;
	}

	@Override
	public Message editMessage(int messageId, String content) {
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria(MessageEntity.class);
		MessageEntity messageEntity = (MessageEntity) criteria.add(Restrictions.eq("id", messageId)).uniqueResult();
		
		messageEntity.setContent(content);
		
		return (Message) messageEntity;
	}

	@Override
	public Message getMessageById(int messageId) {
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria(MessageEntity.class);
		MessageEntity messageEntity = (MessageEntity) criteria.add(Restrictions.eq("id", messageId)).uniqueResult();
		
		return (Message) messageEntity;
	}
	
	@Override
	public List<Message> getAllMessagesFromChat(int chatId) {
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria(MessageEntity.class);
		List<Message> messageEntity = (List<Message>) criteria.add(Restrictions.eq("chatId", chatId)).list();
		
		return messageEntity;
	}

	@Override
	public boolean isMessageStored(int messageId) {	
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria(MessageEntity.class);
		MessageEntity messageEntity = (MessageEntity) criteria.add(Restrictions.eq("id", messageId)).uniqueResult();
		
		if (messageEntity == null) {
			return false;
		} else {
			return true;
		}
	}
	
	
	
//	private JdbcTemplate jdbcTemplate;
//    
//    public MessageDaoImpl(DataSource dataSource) {
//        this.jdbcTemplate = new JdbcTemplate(dataSource);
//    }
//
//	@Override
//	public Message sendMessage(int senderId, int chatId, String content) {  
//		Timestamp tms = new Timestamp(System.currentTimeMillis());
//		
//        GeneratedKeyHolder holder = new GeneratedKeyHolder();
//		int numberOfRows = jdbcTemplate.update(new PreparedStatementCreator() {
//			
//			@Override
//		    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//				PreparedStatement statement = con.prepareStatement(
//						"insert into message(sender_id, chat_room_id, content, create_tms, update_tms) values (?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
//				
//		        statement.setInt(1, senderId);
//		        statement.setInt(2, chatId);
//		        statement.setString(3, content);
//		        statement.setTimestamp(4, tms);
//		        statement.setTimestamp(5, tms);
//		        
//		        return statement;
//			}
//		}, holder);
//    	
//		if (numberOfRows > 0) {
//			int messageId = holder.getKey().intValue();
//			
//			if (messageId > 0) {
//				Message message = new Message();
//				
//				message.setId(messageId);
//				message.setSenderId(senderId);
//				message.setChatId(chatId);
//				message.setContent(content);
//				message.setCreateTms(tms);
//		        message.setUpdateTms(tms);
//		        
//				return message;
//			} else {
//				throw new RuntimeException("Problem with the primary key!");
//			}
//		} else {
//			throw new RuntimeException("Cannot insert into message!");
//		}
//	}
//	
//	@Override
//	public Message editMessage(int messageId, String content) {
//        String updateMessage = "update message set content=? where id=?";
//		Object[] messageValues = new Object[] { content, messageId };
//		
//		int numberOfRows = jdbcTemplate.update(updateMessage, messageValues);
//		
//		if (numberOfRows > 0) {
//			return getMessageById(messageId);
//		} else {
//			throw new RuntimeException("Cannot update the content!");
//		}
//	}
//	
//	@Override
//	public Message getMessageById(int messageId) {
//		String selectMessage = "select * from message where id=?";
//    	Object[] messageValues = new Object[] { messageId };
//    	
//    	Message message = jdbcTemplate.queryForObject(selectMessage, messageValues,  new RowMapper<Message>() {
//    		 
//    		public Message mapRow(ResultSet rs, int rowNum) throws SQLException {
//    			Message message = new Message();
//	    		
//    			message.setId(messageId);
//    			message.setSenderId(rs.getInt("sender_id"));
//				message.setChatId(rs.getInt("chat_room_id"));
//				message.setContent(rs.getString("content"));
//				message.setCreateTms(rs.getTimestamp("create_tms"));
//				message.setUpdateTms(rs.getTimestamp("update_tms"));
//	    		
//	    		return message;
//    		}
//    		
//    	});
//    	
//    	return message;
//	}
//
//	@Override
//	public List<Message> getAllMessagesFromChat(int chatId) {
//		List<Message> listOfMessages = jdbcTemplate.query("select * from message where chat_room_id=?", new Object[] { chatId }, new RowMapper<Message>() {
//	 
//			@Override
//	    	public Message mapRow(ResultSet rs, int rowNumber) throws SQLException {
//				Message message = new Message();
//				
//				message.setId(rs.getInt("id"));
//				message.setSenderId(rs.getInt("sender_id"));
//				message.setChatId(rs.getInt("chat_room_id"));
//				message.setContent(rs.getString("content"));
//				message.setCreateTms(rs.getTimestamp("create_tms"));
//				message.setUpdateTms(rs.getTimestamp("update_tms"));
//
//				return message;
//			}
//	     
//		});
//	
//		return listOfMessages;
//	}
//
//	@Override
//	public boolean isMessageStored(int messageId) {
//		String sql = "select id from message where id=?";
//        Object[] values = new Object[] { messageId };
//        List<Integer> messageIds = jdbcTemplate.queryForList(sql, values, Integer.class);
//        
//        if (messageIds.size() > 1) {
//        	throw new RuntimeException("DB incosistency");
//        } else if (messageIds.size() == 1) {
//        	return true;
//        }
//
//		return false;
//	}
	
}
