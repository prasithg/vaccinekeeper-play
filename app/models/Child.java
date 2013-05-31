package models;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.JacksonDBCollection;
import net.vz.mongodb.jackson.ObjectId;
import play.modules.mongodb.jackson.MongoDB;

import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;

public class Child {
	
	@ObjectId
	public String _id;

	public String firstName;
	
	public Long dob;
	
//	TODO: Need to create an index for userId, so the getChildren method is fast
	public String userId;
	
	public enum Sex {MALE, FEMALE}
	
	public Sex sex;
	
	public List<Schedule> schedule;

	public List<Stat> stats;
	
	public Child(){
	}

	public Child(String userId, String firstName, long dob, Sex sex){
		this.userId = userId;
		this.firstName = firstName;
		this.dob = dob;
		createSchedule();
		this.sex = sex;
	}

	public Child(String firstName, long dob, Sex sex){
		this(null, firstName, dob, sex);
	}
	
	private static JacksonDBCollection<Child, String> childColl() {
		return MongoDB.getCollection("Children", Child.class, String.class);
	}

	private static JacksonDBCollection<Vaccine, String> vaccineColl() {
		return MongoDB.getCollection("Vaccines", Vaccine.class, String.class);
	}

	public String validateNew(){
    	if(userId==null | firstName==null | dob==null | sex==null) return "Child is missing initialization parameters";
    	return null;
	}

	/**
	 * Updates the current child object with the new child's parameters
	 * @param child
	 */
	public Child updateDetails(Child child){
		if(child.firstName!=null) firstName = child.firstName;
		if(child.sex!=null) sex = child.sex;		
		if(child.dob!=null) dob = child.dob;
		return this;
	}

	
	/**
	 * Create a brand new schedule for the Child object, this method will not persist the child object
	 * @param
	 * @return a child with the schedule or null if the child object already has a schedule, or the child object has no dob
	 */
	public Child createSchedule(){
		if(schedule!=null) return null;
		if(dob==null) return null;
		
		schedule = new LinkedList<Schedule>();
		DBCursor<Vaccine> vaccines = vaccineColl().find();
		while(vaccines.hasNext()){
			Vaccine vaccine = vaccines.next();
			Iterator<Shot> shots = vaccine.shots.iterator();
			while (shots.hasNext())
				schedule.add(new Schedule(vaccine, shots.next().no, dob));
		}
		return this;
	}

	public static Child findOneById(String id) {
		Child child = null;
		try{
			child = childColl().findOneById(id);
		} catch (IllegalArgumentException e) {
			return null;
		}
		return child;
	}

	public static DBCursor<Child> getChildren (String userId){
		return childColl().find().is("userId",userId).limit(10).sort(new BasicDBObject("firstName", 1));
	}
	
	
	public static Child findOne(){
		return childColl().findOne();
	}

	public static Child create(Child child) {
	    return childColl().save(child).getSavedObject();
	}
	
	/**
	 * Persistence
	 * @param child
	 * @return
	 */
	public static Child update(Child child){
		return childColl().save(child).getSavedObject();
	}

	/**
	 * 
	 * @param childId
	 * @return true if deleted false if not
	 */
	public static boolean delete(String childId) {
	    Child child = null;
	    try{
		    child = childColl().findOneById(childId);	    	
	    } catch (IllegalArgumentException e) {
	    	System.out.println("Could not find child +"+childId);
	    	return false;
	    }
	    try{
	    	childColl().remove(child);
	    } catch (MongoException e){
	    	return false;
	    }
	    return true;
	}

	public static void drop() {
	    childColl().drop();
	}

	public static boolean isEmpty(){
		return childColl().findOne() == null ? true : false;
	}



}