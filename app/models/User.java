package models;

import java.util.List;

import javax.validation.Constraint;

import com.mongodb.MongoException;

import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.JacksonDBCollection;
import net.vz.mongodb.jackson.ObjectId;
import net.vz.mongodb.jackson.WriteResult;
import play.data.format.Formats;
import play.data.validation.Constraints;
import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.EmailValidator;
import play.data.validation.Constraints.Required;
import play.modules.mongodb.jackson.MongoDB;
//import org.bson.types.ObjectId;

public class User {

	@ObjectId
	public String _id;
	
	public String userNameEmail;

	public String password;
	
	public String firstName, lastName;
	
   	public List<String> childIds;
   	
	public User(){
	}
	
	/**
	 * A validation method
	 * Use it after creating a user object with your new data
	 * @return Description of the error if there's a problem, null if no error
	 */
    public String validate() {
    	if(userNameEmail.isEmpty()) return "userName field is empty";
    	if(password.isEmpty()) return "password field is empty";
    	if(password.length()<6) return "password must be at least 6 characters long";
    	
		EmailValidator valid = new EmailValidator();
		if(!valid.isValid(userNameEmail)) return "invalid email format";

		User user = User.findByUserName(userNameEmail);
		if(user!=null) return "A user with the email "+userNameEmail+" already exists";
		
        return null;
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
		DBCursor<User>  cursor = userColl().find(DBQuery.is("userNameEmail", userNameEmail));
		if(!cursor.hasNext()) return null;
		return cursor.next();
	}
	
	public static User create(User user) throws MongoException{
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
