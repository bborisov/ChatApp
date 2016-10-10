package com.ibm.dao.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import com.ibm.dao.UserDao;
import com.ibm.entities.UserEntity;
import com.ibm.other.User;

@SuppressWarnings("unchecked")
@Transactional
public class UserDaoImpl implements UserDao {

	@Autowired
	SessionFactory sessionFactory;
	
	@Override
	public User createUser(String userName, String email, int statusId) {
		UserEntity userEntity = new UserEntity(email, userName, statusId);
		
		Session session = sessionFactory.getCurrentSession();
		session.persist(userEntity);
		
		return (User) userEntity;
	}

	@Override
	public User getUserByEmail(String email) {
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria(UserEntity.class);
		UserEntity userEntity = (UserEntity) criteria.add(Restrictions.like("email", email)).uniqueResult();
		
		return (User) userEntity;
	}

	@Override
	public User getUserById(int userId) {
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria(UserEntity.class);
		UserEntity userEntity = (UserEntity) criteria.add(Restrictions.eq("id", userId)).uniqueResult();
		
		return (User) userEntity;
	}

	@Override
	public List<User> getAllUsers() {
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria(UserEntity.class);
		List<UserEntity> listOfUserEntities = (List<UserEntity>) criteria.list();
		
		List<User> listOfUsers = listOfUserEntities.stream().map(u -> u.toUser()).collect(Collectors.toList());
		
		return listOfUsers;
	}

	@Override
	public boolean isEmailStored(String email) {
		Session session = sessionFactory.getCurrentSession();
		
		Criteria criteria = session.createCriteria(UserEntity.class);
		UserEntity userEntity = (UserEntity) criteria.add(Restrictions.like("email", email)).uniqueResult();
		
		if (userEntity  == null) {
			return false;
		} else {
			return true;
		}
	}
	
//    private JdbcTemplate jdbcTemplate;
//     
//    public UserDaoImpl(DataSource dataSource) {
//        this.jdbcTemplate = new JdbcTemplate(dataSource);
//    }
//    
//    @Override
//    public User createUser(String userName, String email, int statusId) {
//    	GeneratedKeyHolder holder = new GeneratedKeyHolder();
//		int numberOfRows = jdbcTemplate.update(new PreparedStatementCreator() {
//			
//			@Override
//		    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
//				PreparedStatement statement = con.prepareStatement(
//						"insert into user(name, email, status_id) values (?,?,?)", Statement.RETURN_GENERATED_KEYS);
//				
//		        statement.setString(1, userName);
//		        statement.setString(2, email);
//		        statement.setInt(3, statusId);
//		        return statement;
//			}
//		}, holder);
//    	
//		if (numberOfRows > 0) {
//			int userId = holder.getKey().intValue();
//			
//			if (userId > 0) {
//				User user = new User();
//				
//				user.setId(userId);
//				user.setName(userName);
//				user.setEmail(email);
//				user.setStatusId(statusId);
//				
//				return user;
//			} else {
//				throw new RuntimeException("Problem with the primary key!");
//			}
//		} else {
//			throw new RuntimeException("Cannot insert into user!");
//		}
//    }
//    
//    @Override
//    public User getUserByEmail(String email) {
//    	String selectUser = "select id, name, status_id from user where email like ?";
//    	Object[] userValues = new Object[] { email };
//    	
//    	User user = null;
//    	
//    	try {
//	    	user = jdbcTemplate.queryForObject(selectUser, userValues,  new RowMapper<User>() {
//	    		 
//	    		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
//		    		User user = new User();
//		    		
//		    		user.setId(rs.getInt("id"));
//		    		user.setName(rs.getString("name"));
//		    		user.setEmail(email);
//		    		user.setStatusId(rs.getInt("status_id"));
//		    		
//		    		return user;
//	    		}
//	    		
//	    	});
//	    	return user;
//    	} catch (EmptyResultDataAccessException erdae) {
//    		//return empty object
//    		return user;
//    	}
//    }
//    
//    @Override
//    public User getUserById(int userId) {
//    	String selectUser = "select name, email, status_id from user where id=?";
//    	Object[] userValues = new Object[] { userId };
//    	
//    	User user = jdbcTemplate.queryForObject(selectUser, userValues,  new RowMapper<User>() {
//    		 
//    		public User mapRow(ResultSet rs, int rowNum) throws SQLException {
//	    		User user = new User();
//	    		
//	    		user.setId(userId);
//	    		user.setName(rs.getString("name"));
//	    		user.setEmail(rs.getString("email"));
//	    		user.setStatusId(rs.getInt("status_id"));
//	    		
//	    		return user;
//    		}
//    		
//    	});
//    	
//    	return user;
//    }
//	
//	@Override
//    public List<User> getAllUsers() {
//        String selectUsers = "select * from user";
//        List<User> listOfUsers = jdbcTemplate.query(selectUsers, new RowMapper<User>() {
// 
//            @Override
//            public User mapRow(ResultSet rs, int rowNumber) throws SQLException {
//                User user = new User();
//                
//                user.setId(rs.getInt("id"));
//                user.setName(rs.getString("name"));
//                user.setEmail(rs.getString("email"));
//                user.setStatusId(rs.getInt("status_id"));
//                
//                return user;
//            }
//             
//        });
//        
//        return listOfUsers;
//    }
//
//	@Override
//	public boolean isEmailStored(String email) {
//		String selectUserIds = "select id from user where email like ?";
//        Object[] userValues = new Object[] { email };
//        List<Integer> userIds = jdbcTemplate.queryForList(selectUserIds, userValues, Integer.class);
//        
//        if (userIds.size() > 1) {
//        	throw new RuntimeException("DB incosistency");
//        } else if (userIds.size() == 1) {
//        	return true;
//        }
//
//        return false;
//	}
	
}