package com.ibm.dao.impl;

import java.sql.Timestamp;
import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;

import com.ibm.dao.ChatDao;
import com.ibm.entities.ChatEntity;
import com.ibm.entities.MembershipEntity;
import com.ibm.other.Chat;
import com.ibm.other.ChatType;
import com.ibm.other.MemberDto;
import com.ibm.other.MemberRole;
import com.ibm.other.MemberStatus;

@Transactional
@SuppressWarnings("unchecked")
public class ChatDaoImpl implements ChatDao {
	
	@Autowired
	SessionFactory sessionFactory;

	@Override
	public Chat createChat(String chatName, String summary, int typeId, int creatorId) {
		
		Timestamp tms = new Timestamp(System.currentTimeMillis());
		ChatEntity chatEntity = new ChatEntity(tms, creatorId, chatName, summary, typeId, tms);
			
		Session session = sessionFactory.getCurrentSession();
		session.persist(chatEntity);
		
		joinChat(creatorId, chatEntity.getId());
		makeAdmin(creatorId, chatEntity.getId());
		
		return (Chat) chatEntity;
	}

	@Override
	public void joinChat(int userId, int chatId) {
		MembershipEntity membershipEntity = new MembershipEntity(MemberRole.USER, MemberStatus.SELF_JOINED, chatId, userId);
		
		Session session = sessionFactory.getCurrentSession();
		session.persist(membershipEntity);
	}

	@Override
	public void updateMemberStatus(int userId, int chatId, int statusId) {
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria(MembershipEntity.class);
		MembershipEntity membershipEntity = (MembershipEntity) criteria.add(Restrictions.eq("userId", userId)).add(Restrictions.eq("chatId", chatId)).uniqueResult();
		
		membershipEntity.setStatusId(statusId);
	}

	@Override
	public void kickFromChat(int userId, int chatId) {
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria(MembershipEntity.class);
		MembershipEntity membershipEntity = (MembershipEntity) criteria.add(Restrictions.eq("userId", userId)).add(Restrictions.eq("chatId", chatId)).uniqueResult();
		
		session.delete(membershipEntity);
	}

	@Override
	public void makeAdmin(int userId, int chatId) {
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria(MembershipEntity.class);
		MembershipEntity membershipEntity = (MembershipEntity) criteria.add(Restrictions.eq("userId", userId)).add(Restrictions.eq("chatId", chatId)).uniqueResult();
		
		membershipEntity.setRoleId(MemberRole.ADMIN);
	}

	
	@Override
	public List<MemberDto> getAllMembersFromChats(int chatId) {
		
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria(MembershipEntity.class, "membership");
		criteria.createAlias("membership.user", "user");
		
		ProjectionList proList = Projections.projectionList();
        proList.add(Projections.property("user.id"), "id");
        proList.add(Projections.property("user.name"), "name");
        proList.add(Projections.property("user.email"), "email");
        proList.add(Projections.property("user.statusId"), "statusId");
        proList.add(Projections.property("membership.roleId"), "roleId");
        criteria.setProjection(proList);
        
        criteria.setResultTransformer(Transformers.aliasToBean(MemberDto.class));
        
        DetachedCriteria detachedCriteria = DetachedCriteria.forClass(MembershipEntity.class);
		detachedCriteria.add(Restrictions.eq("chatId", chatId)).add(Restrictions.ne("statusId", MemberStatus.INVITED)).setProjection(Projections.property("userId"));
        
		List<MemberDto> list = criteria.add(Restrictions.and(Restrictions.eq("membership.chatId", chatId), Subqueries.propertyIn("user.id", detachedCriteria))).list();
        
		return list;
	}

	@Override
	public List<Chat> getAllPermittedChats(int userId) {	
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria(ChatEntity.class);
		
		DetachedCriteria detachedCriteria = DetachedCriteria.forClass(MembershipEntity.class);
		detachedCriteria.add(Restrictions.eq("userId", userId)).setProjection(Projections.property("chatId"));
		
		List<Chat> listOfChatEntities = (List<Chat>) criteria.add(Restrictions.or(Restrictions.eq("typeId", ChatType.PUBLIC), Subqueries.propertyIn("id", detachedCriteria))).list();
		
		//List<Chat> listOfChats = listOfChatEntities.stream().map(c -> c.toChat()).collect(Collectors.toList());
		
		return listOfChatEntities;
	}

	@Override
	public int getStatusId(int userId, int chatId) {
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria(MembershipEntity.class);
		MembershipEntity membershipEntity =  (MembershipEntity) criteria.add(Restrictions.eq("userId", userId)).add(Restrictions.eq("chatId", chatId)).uniqueResult();
		
		if (membershipEntity == null) {
			return 0;
		}
		
		return membershipEntity.getStatusId();
	}

	@Override
	public boolean isMember(int userId, int chatId) {
		int statusId = getStatusId(userId, chatId);
		if (statusId == MemberStatus.SELF_JOINED || statusId == MemberStatus.JOINED_BY_INVITATION) {
			return true;
		}
			
		return false;
	}
	
	@Override
	public boolean isInvited(int userId, int chatId) {
		int statusId = getStatusId(userId, chatId);
		if (statusId == MemberStatus.INVITED) {
			return true;
		}
			
		return false;
	}
	
	@Override
	public boolean isAdmin(int roleId) {
		if (roleId == MemberRole.ADMIN) {
			return true;
		}

		return false;
	}

//	private JdbcTemplate jdbcTemplate;
//
//	public ChatDaoImpl(DataSource dataSource) {
//		this.jdbcTemplate = new JdbcTemplate(dataSource);
//	}
//
//	@Override
//	public Chat createChat(String chatName, String summary, int typeId, int creatorId) {
//		Timestamp tms = new Timestamp(System.currentTimeMillis());
//		
//		GeneratedKeyHolder holder = new GeneratedKeyHolder();
//		int numberOfRows = jdbcTemplate.update(new PreparedStatementCreator() {
//			
//			@Override
//		    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//				PreparedStatement statement = con.prepareStatement(
//						"insert into chat_room(name, summary, type_id, creator_id, create_tms, update_tms)"
//						+ "values (?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
//				
//		        statement.setString(1, chatName);
//		        statement.setString(2, summary);
//		        statement.setInt(3, typeId);
//		        statement.setInt(4, creatorId);     
//		        statement.setTimestamp(5, tms);
//		        statement.setTimestamp(6, tms);
//		        return statement;
//			}
//		}, holder);
//		
//		if (numberOfRows > 0) {
//			int chatId = holder.getKey().intValue();
//			
//			if (chatId > 0) {
//				joinChat(creatorId, chatId);
//				makeAdmin(creatorId, chatId);
//				
//				Chat chat = new Chat();
//				
//				chat.setId(chatId);
//				chat.setName(chatName);
//				chat.setSummary(summary);
//				chat.setTypeId(typeId);
//				chat.setCreatorId(creatorId);
//				chat.setCreateTms(tms);
//				chat.setUpdateTms(tms);
//				
//				return chat;				
//			} else {
//				throw new RuntimeException("Problem with the primary key!");
//			}
//		} else {
//			throw new RuntimeException("Cannot insert into chat_room!");
//		}
//	}
//	
//	@Override
//	public void joinChat(int userId, int chatId) {
//		String insertIntoMembership = "insert into membership(user_id, chat_room_id, role_id, status_id) values(?,?,?,?)";
//		Object[] membershipValues = new Object[] { userId, chatId, MemberRole.USER, MemberStatus.SELF_JOINED };
//		
//		int numberOfRows = jdbcTemplate.update(insertIntoMembership, membershipValues);
//		
//		if (numberOfRows > 0) {
//			return;
//		} else {
//			throw new RuntimeException("Cannot insert into membership!");
//		}
//	}
//	
//	@Override
//	public void updateMemberStatus(int userId, int chatId, int statusId) {
//		String updateMembership = "update membership set status_id=? where user_id=? and chat_room_id=?";
//		Object[] membershipValues = new Object[] { statusId, userId, chatId };
//
//		int numberOfRows = jdbcTemplate.update(updateMembership, membershipValues);
//
//		if (numberOfRows > 0) {
//			return;
//		} else {
//			throw new RuntimeException("Cannot update the status in membership!");
//		}
//	}
//
//	@Override
//	public void kickFromChat(int userId, int chatId) {
//		String deliteFromMembership = "delete from membership where user_id=? and chat_room_id=?";
//		Object[] membershipValues = new Object[] { userId, chatId };
//
//		int numberOfRows = jdbcTemplate.update(deliteFromMembership, membershipValues);
//
//		if (numberOfRows > 0) {			
//			return;
//		} else {
//			throw new RuntimeException("Cannot delite from membership!");
//		}
//	}
//	
//	@Override
//	public void makeAdmin(int userId, int chatId) {
//		String updateMembership = "update membership set role_id=? where user_id=? and chat_room_id=?";
//		Object[] membershipValues = new Object[] { MemberRole.ADMIN, userId, chatId };
//
//		int numberOfRows = jdbcTemplate.update(updateMembership, membershipValues);
//
//		if (numberOfRows > 0) {
//			return;
//		} else {
//			throw new RuntimeException("Cannot update the role!");
//		}
//	}
//	
//	@Override
//	public List<MemberDto> getAllMembersFromChats(int chatId) {
//		List<MemberDto> listOfMembers = jdbcTemplate.query("select u.id, u.name, u.email, u.status_id, m.role_id from user u join membership m on m.user_id = u.id" 
//				+ " where m.chat_room_id=? and u.id in (select user_id from membership where chat_room_id=? and status_id<>?)",
//				new Object[] { chatId, chatId, MemberStatus.INVITED }, new RowMapper<MemberDto>() {
//					
//					
//		
//			@Override
//			public MemberDto mapRow(ResultSet rs, int rowNumber) throws SQLException {
//				
//				MemberDto memberDto = new MemberDto();
//				
//				memberDto.setId(rs.getInt("id"));
//				memberDto.setName(rs.getString("name"));
//				memberDto.setEmail(rs.getString("email"));
//				memberDto.setStatusId(rs.getInt("status_id"));
//				memberDto.setRoleId(rs.getInt("role_id"));
//				
//				return memberDto;
//			}
//
//		});
//
//		return listOfMembers;
//	}
//
//	@Override
//	public List<Chat> getAllPermittedChats(int userId) {
//		
//		List<Chat> listOfChats = jdbcTemplate.query("select * from chat_room where type_id=? or id in"
//				+ "(select chat_room_id from membership where user_id=?)", new Object[] { ChatType.PUBLIC, userId }, new RowMapper<Chat>() {
//
//			@Override
//			public Chat mapRow(ResultSet rs, int rowNumber) throws SQLException {
//				
//				Chat chat = new Chat();
//				
//				chat.setId(rs.getInt("id"));
//				chat.setName(rs.getString("name"));
//				chat.setSummary(rs.getString("summary"));
//				chat.setTypeId(rs.getInt("type_id"));
//				chat.setCreatorId(rs.getInt("creator_id"));
//				chat.setCreateTms(rs.getTimestamp("create_tms"));
//				chat.setUpdateTms(rs.getTimestamp("update_tms"));
//				
//				return chat;
//			}
//
//		});
//
//		return listOfChats;
//	}
//	
//	@Override
//	public int getStatusId(int userId, int chatId) {
//		String selectStatusIds = "select status_id from membership where user_id=? and chat_room_id=?";
//		Object[] statusIdsValues = new Object[] { userId, chatId };
//		List<Integer> statusIds = jdbcTemplate.queryForList(selectStatusIds, statusIdsValues, Integer.class);
//
//		if (statusIds.size() > 1) {
//			throw new RuntimeException("DB incosistency");
//		} else if (statusIds.size() == 1) {
//			return statusIds.get(0);
//		}
//		
//		return 0;
//	}
//	
//	@Override
//	public boolean isMember(int userId, int chatId) {
//		int statusId = getStatusId(userId, chatId);
//		if (statusId == MemberStatus.SELF_JOINED || statusId == MemberStatus.JOINED_BY_INVITATION) {
//			return true;
//		}
//			
//		return false;
//	}
//	
//	@Override
//	public boolean isInvited(int userId, int chatId) {
//		int statusId = getStatusId(userId, chatId);
//		if (statusId == MemberStatus.INVITED) {
//			return true;
//		}
//			
//		return false;
//	}
//	
//	@Override
//	public boolean isAdmin(int roleId) {
//		if (roleId == MemberRole.ADMIN) {
//			return true;
//		}
//
//		return false;
//	}

}
