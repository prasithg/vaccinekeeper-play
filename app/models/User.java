package models;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.mongodb.MongoException;

import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.JacksonDBCollection;
import net.vz.mongodb.jackson.ObjectId;
import net.vz.mongodb.jackson.WriteResult;
import play.data.validation.Constraints.EmailValidator;
import play.modules.mongodb.jackson.MongoDB;
//import org.bson.types.ObjectId;

public class User {

	@ObjectId
	public String _id;
	
	public String userNameEmail;

	public String password, newPass;
	
	public String firstName, lastName;
	
	public long dateCreated;
	
   	public List<String> childIds;
   	
	public User(){
	}
	
	public User(String userNameEmail, String password){
		this.userNameEmail=userNameEmail;
		this.password=password;
	}

	/**
	 * A validation method
	 * Use it after creating a user object with your new data
	 * @return Description of the error if there's a problem, null if no error
	 */
    public String validateNew() {
    	if(userNameEmail.isEmpty()) return "userName field is empty";
    	if(password.isEmpty()) return "password field is empty";
    	if(password.length()<6) return "password must be at least 6 characters long";
    	
		EmailValidator valid = new EmailValidator();
		if(!valid.isValid(userNameEmail)) return "invalid email format";

		User user = User.findByName(userNameEmail);
		if(user!=null) return "A user with the email "+userNameEmail+" already exists";
		
        return null;
    }
    
    public String validateExisting(){
    	if(!User.findOneById(_id).password.equals(password)) return "Wrong password";
    	return null;
    }

	public String updateDetails(User user){
		if(user.userNameEmail!=null){
			if(User.findByName(user.userNameEmail)!=null)
				return "userName already exists";
			userNameEmail = user.userNameEmail;
		}
		if(user.firstName!=null) firstName = user.firstName;
		if(user.lastName!=null) lastName = user.lastName;
		if(user.newPass!=null) password = user.newPass;
		return null;
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

	public static User findByName(String userNameEmail){
		DBCursor<User>  cursor = userColl().find(DBQuery.is("userNameEmail", userNameEmail));
		if(!cursor.hasNext()) return null;
		return cursor.next();
	}
	
	public static User create(User user) {
		user.dateCreated = new Date().getTime();
	    return userColl().save(user).getSavedObject();
	}
	
	public static String update(User user){
		return userColl().save(user).getSavedId();
	}
	
//	TODO need something here to confirm that the deletion happened
	public static void delete(String id) {
	    User user = null;
	    try{
		    user = userColl().findOneById(id);	    	
	    } catch (IllegalArgumentException e) {
	    	System.out.println("User.delete() could not find user +"+id);
	    	return;
	    }
	    
	    Iterator<String> children = new LinkedList<String>().iterator();
	    if(user.childIds!=null)
	    	children = user.childIds.iterator();
	    while (children.hasNext()){
	    	Child.delete(children.next());
	    }
	    userColl().remove(user);
	}
	
	public static void drop() {
	    userColl().drop();
	}

	
}
