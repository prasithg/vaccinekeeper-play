package models;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.mongodb.MongoException;

import models.Child.Sex;
import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.DBUpdate;
import net.vz.mongodb.jackson.JacksonDBCollection;
import net.vz.mongodb.jackson.ObjectId;
import net.vz.mongodb.jackson.WriteResult;
import play.data.validation.Constraints.EmailValidator;
import play.data.validation.Constraints.Required;
import play.modules.mongodb.jackson.MongoDB;
//import org.bson.types.ObjectId;

public class User {

	@ObjectId
	public String _id;
	
	public String email;

	public String password, newPass;
	
	public String firstName, lastName;
	
	public Long dateCreated;
	
//	TODO: Add PaymentOption into the object definition
	public enum PaymentOption {FREE, ONEOFF, REGISTERED}
	
	public PaymentOption payment;
	
	public User(){
	}
	
	public User(String userNameEmail, String password){
		this.email=userNameEmail;
		this.password=password;
		this.dateCreated=new Date().getTime();
	}

	private static JacksonDBCollection<User, String> userColl() {
		return MongoDB.getCollection("Users", User.class, String.class);
	}

	/**
	 * A validation method
	 * Use it after creating a user object with your new data
	 * @return Description of the error if there's a problem, null if no error
	 */
    public String validateNew() {
    	if(email.isEmpty()) return "userName field is empty";
    	if(password.isEmpty()) return "password field is empty";
    	if(password.length()<6) return "password must be at least 6 characters long";
    	
		EmailValidator valid = new EmailValidator();
		if(!valid.isValid(email)) return "invalid email format";

		User user = User.findByName(email);
		if(user!=null) return "A user with the email "+email+" already exists";
		
        return null;
    }
    
    public String validateExisting(){
    	if(!User.findByName(email).password.equals(password)) return "Wrong password";
    	return null;
    }

	public String updateDetails(User user){
		if(user.email!=null){
			if(User.findByName(user.email)!=null)
				return "userName already exists";
			email = user.email;
		}
		if(user.firstName!=null) firstName = user.firstName;
		if(user.lastName!=null) lastName = user.lastName;
		if(user.newPass!=null) password = user.newPass;
		return null;
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

	public static User findByName(String email){
		DBCursor<User>  cursor = userColl().find(DBQuery.is("email", email));
		if(!cursor.hasNext()) return null;
		return cursor.next();
	}
	
	public static User create(User user) {
		user.dateCreated = new Date().getTime();
	    return userColl().save(user).getSavedObject();
	}
	
	public static User update(User user){
		return userColl().save(user).getSavedObject();
	}
	
	/**
	 * Deletes the user and the user's children
	 * @param userId
	 * @return true if deleted, false if not deleted
	 */
	public static boolean delete(String userId) {
	    User user = null;
	    try{
		    user = userColl().findOneById(userId);	    	
	    } catch (IllegalArgumentException e) {
	    	return false;
	    }
	    
	    List<Child> children = Child.getChildren(userId).toArray();
	    if(children.size()!=0){
	    	Iterator<Child> it = children.iterator();
	    	while (it.hasNext()){
	    		boolean deleted = Child.delete(it.next()._id);
	    		if (deleted == false) return false;
	    	}
	    }
	    
	    try{
	    	userColl().remove(user);
	    } catch (MongoException e){
	    	return false;
	    }

	    return true;
	}
	
	public static void drop() {
	    userColl().drop();
	}

	
}
