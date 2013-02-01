package models;

import java.util.List;

import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.JacksonDBCollection;
import net.vz.mongodb.jackson.ObjectId;
import net.vz.mongodb.jackson.WriteResult;
import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Required;
import play.modules.mongodb.jackson.MongoDB;
//import org.bson.types.ObjectId;

public class User {

	@ObjectId
	public String _id;
	
	@Required @Email
	public String userNameEmail;

	@Required
	public String password;
	
	public String firstName, lastName;
	
   	public List<String> childIds;
   	
	public User(){
	}
	
	public User(String userNameEmail, String password){
		this.userNameEmail=userNameEmail;
		this.password=password;
	}
	
	private static JacksonDBCollection<User, String> userColl() {
		return MongoDB.getCollection("Users", User.class, String.class);
	}

	public static void addChild(String userId, String childId){
		userColl().updateById(userId, DBUpdate.push("childIds", childId));		
	}

	public static boolean isEmpty(){
		return userColl().findOne() == null ? true : false;
	}

	public static List<User> all() {
	    return userColl().find().toArray();
	}
	
	public static User findOne(){
		return userColl().findOne();
	}

	public static User findOneById(String id){
		return userColl().findOneById(id);
	}

	public static User findByUserName(String userNameEmail){
		return userColl().find(DBQuery.is("userNameEmail", userNameEmail)).toArray().get(0);
	}
	
	public static User create(User user) {
	    return userColl().save(user).getSavedObject();
	}
	
	public static String update(User user){
		return userColl().save(user).getSavedId();
	}
	
	public static void delete(String id) {
	    User task = userColl().findOneById(id);
	    if (task != null)
	        userColl().remove(task);
	}
	
	public static void drop() {
	    userColl().drop();
	}

}
