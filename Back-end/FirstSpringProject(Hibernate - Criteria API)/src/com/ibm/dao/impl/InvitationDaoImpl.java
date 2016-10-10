package com.ibm.dao.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import com.ibm.dao.ChatDao;
import com.ibm.dao.InvitationDao;
import com.ibm.dao.UserDao;
import com.ibm.entities.InvitationEntity;
import com.ibm.other.Invitation;
import com.ibm.other.InvitationStatus;
import com.ibm.other.MemberStatus;
import com.ibm.other.User;

@SuppressWarnings("unchecked")
@Transactional
public class InvitationDaoImpl implements InvitationDao {

	@Autowired
	SessionFactory sessionFactory;
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	private ChatDao chatDao;
	
	@Override
	public Invitation inviteToChat(int userId, int chatId, int invitorId) {
		InvitationEntity invitationEntity = new InvitationEntity(InvitationStatus.PENDING, chatId, userId, invitorId);
		
		Session session = sessionFactory.getCurrentSession();
		session.persist(invitationEntity);
		
		return (Invitation) invitationEntity;
	}

	@Override
	public User acceptInvitation(int userId, int chatId, int invitationId) {
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria(InvitationEntity.class);
		InvitationEntity invitationEntity = (InvitationEntity) criteria.add(Restrictions.eq("id", invitationId)).uniqueResult();
		
		invitationEntity.setStatusId(InvitationStatus.ACCEPTED);
		
		chatDao.updateMemberStatus(userId, chatId, MemberStatus.JOINED_BY_INVITATION);
		return userDao.getUserById(userId);
	}

	@Override
	public void declineInvitation(int userId, int chatId, int invitationId) {
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria(InvitationEntity.class);
		InvitationEntity invitationEntity = (InvitationEntity) criteria.add(Restrictions.eq("id", invitationId)).uniqueResult();
		
		invitationEntity.setStatusId(InvitationStatus.DECLINED);
		chatDao.kickFromChat(userId, chatId);
	}

	@Override
	public List<Invitation> getAllInvitationsForUser(int userId) {
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria(InvitationEntity.class);
		List<Invitation> listOfInvitations = (List<Invitation>) criteria.add(Restrictions.eq("userId", userId)).list();
		
		return listOfInvitations;
	}
	
//	private JdbcTemplate jdbcTemplate;
//	
//	@Autowired
//	private UserDao userDao;
//	
//	@Autowired
//	private ChatDao chatDao;
//	
//	public InvitationDaoImpl(DataSource dataSource) {
//		this.jdbcTemplate = new JdbcTemplate(dataSource);
//	}
//	
//	@Override
//	public Invitation inviteToChat(int userId, int chatId, int invitorId) {
//		GeneratedKeyHolder holder = new GeneratedKeyHolder();
//		int numberOfRows = jdbcTemplate.update(new PreparedStatementCreator() {
//			
//			@Override
//		    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//				PreparedStatement statement = con.prepareStatement(
//						"insert into invitation(user_id, chat_room_id, invitor_id, status_id) values(?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
//				
//		        statement.setInt(1, userId);
//		        statement.setInt(2, chatId);
//		        statement.setInt(3, invitorId);
//		        statement.setInt(4, InvitationStatus.PENDING);
//		        return statement;
//			}
//		}, holder);
//    	
//		if (numberOfRows > 0) {
//			int invitationId = holder.getKey().intValue();
//			
//			if (invitationId > 0) {
//				Invitation invitation = new Invitation();
//				
//				invitation.setId(invitationId);
//				invitation.setUserId(userId);
//				invitation.setChatId(chatId);
//				invitation.setInvitorId(invitorId);
//				invitation.setStatusId(InvitationStatus.PENDING);
//				
//				return invitation;
//			} else {
//				throw new RuntimeException("Problem with the primary key!");
//			}
//		} else {
//			throw new RuntimeException("Cannot insert into invitation!");
//		}
//	}
//
//	@Override
//	public User acceptInvitation(int userId, int chatId, int invitationId) {
//		String updateInvitation = "update invitation set status_id=? where id=?";
//		Object[] invitationValues = new Object[] { InvitationStatus.ACCEPTED, invitationId };
//
//		int numberOfRowsInvitation = jdbcTemplate.update(updateInvitation, invitationValues);
//
//		if (numberOfRowsInvitation > 0) {
//			chatDao.updateMemberStatus(userId, chatId, MemberStatus.JOINED_BY_INVITATION);
//			return userDao.getUserById(userId);
//		} else {
//			throw new RuntimeException("Cannot update the status in invitation!");
//		}
//	}
//	
//	@Override
//	public void declineInvitation(int userId, int chatId, int invitationId) {
//		String updateInvitation = "update invitation set status_id=? where id=?";
//		Object[] invitationValues = new Object[] { InvitationStatus.DECLINED, invitationId };
//
//		int numberOfRowsInvitation = jdbcTemplate.update(updateInvitation, invitationValues);
//
//		if (numberOfRowsInvitation > 0) {
//			chatDao.kickFromChat(userId, chatId);
//		} else {
//			throw new RuntimeException("Cannot update the status in invitation!");
//		}
//	}
//
//	@Override
//	public List<Invitation> getAllInvitationsForUser(int userId) {
//		String selectInvitations = "select * from invitation where user_id = ?";
//        List<Invitation> listOfInvitations = jdbcTemplate.query(selectInvitations, new Object[] { userId }, new RowMapper<Invitation>() {
// 
//            @Override
//            public Invitation mapRow(ResultSet rs, int rowNumber) throws SQLException {
//            	Invitation invitation = new Invitation();
//                
//                invitation.setId(rs.getInt("id"));
//                invitation.setUserId(userId);
//                invitation.setChatId(rs.getInt("chat_room_id"));
//                invitation.setInvitorId(rs.getInt("invitor_id"));
//                invitation.setStatusId(rs.getInt("status_id"));
//                
//                return invitation;
//            }
//             
//        });
//        
//        return listOfInvitations;
//	}

}
