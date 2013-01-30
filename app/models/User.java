package models;

import java.util.List;

import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.JacksonDBCollection;
import net.vz.mongodb.jackson.ObjectId;
import net.vz.mongodb.jackson.WriteResult;
import play.data.validation.Constraints.Required;
import play.modules.mongodb.jackson.MongoDB;
//import org.bson.types.ObjectId;

public class User {

	@ObjectId
	public String _id;
	
	@Required
	public String userName, password;	
	
   	public List<String> childIds;
	
	public User(){
		//Commented because Michael is an artard
		//this(null, null);
	}
	
	public User(String userName, String password){
		this.userName=userName;
		this.password=password;
	}
	
	
	private static JacksonDBCollection<User, String> userColl() {
		return MongoDB.getCollection("Users", User.class, String.class);
	}

	public static void addChild(String _id, String childId){
		userColl().updateById(_id, DBUpdate.push("childIds", childId));		
	}

	public static boolean isEmpty(){
		return userColl().findOne() == null ? true : false;
	}

	public static List<User> all() {
	    return userColl().find().toArray();
	}
	
	public static User findOne(String id){
		return userColl().findOneById(id);
	}

	public static String create(User user) {
	    WriteResult<User, String> result = userColl().save(user);
	    return result.getSavedId();
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
