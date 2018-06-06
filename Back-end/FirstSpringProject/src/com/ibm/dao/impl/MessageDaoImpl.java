package com.ibm.dao.impl;

import java.sql.Timestamp;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.ibm.dao.MessageDao;
import com.ibm.entities.MessageEntity;
import com.ibm.entities.wrappers.Message;

@SuppressWarnings("unchecked")
public class MessageDaoImpl extends BasicDaoImpl<MessageEntity> implements MessageDao {

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
		MessageEntity messageEntity = getEntityById(messageId);

		messageEntity.setContent(content);
		messageEntity.setUpdateTms(new Timestamp(System.currentTimeMillis()));

		return (Message) messageEntity;
	}

	@Override
	public List<Message> getAllMessagesByChat(int chatId) {
		Session session = sessionFactory.getCurrentSession();

		Criteria criteria = session.createCriteria(MessageEntity.class);
		List<Message> listOfMessages = (List<Message>) criteria.add(Restrictions.eq("chatId", chatId)).list();

		return listOfMessages;
	}

	@Override
	public Class<MessageEntity> getEntityClass() {
		return MessageEntity.class;
	}

}
