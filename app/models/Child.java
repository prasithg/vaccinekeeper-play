package models;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.JacksonDBCollection;
import net.vz.mongodb.jackson.ObjectId;
import play.data.validation.Constraints.Required;
import play.modules.mongodb.jackson.MongoDB;

public class Child {
	
	@ObjectId
	public String _id;

	@Required
	public String firstName;
	
	@Required
	public Long dob;
	
//	TODO: Add reverse link to parent. Should be indexed
	@Required
	public String userId;
	
	public enum Sex {MALE, FEMALE}
	
	@Required
	public Sex sex;
	
	@Required
	public List<Schedule> schedule;

	public List<Stat> stats;
	
	public Child(){
	}

	public Child(String firstName, long dob, Sex sex){
		this.firstName = firstName;
		this.dob = dob;
		createSchedule();
		this.sex = sex;
	}
	
	public String validateNew(){
    	if(firstName==null | dob==null | sex==null) return "Child is missing initialization parameters";
    	return null;
	}

	public String validateExisting(){
    	if(Child.findOneById(_id)==null) return "Child id does not exist";
    	return null;
	}
	
	/**
	 * The 
	 * @param child
	 */
	public void updateDetails(Child child){
		if(child.firstName!=null) firstName = child.firstName;
		if(child.sex!=null) sex = child.sex;		
		if(child.dob!=null) dob = child.dob;
	}

	
	private static JacksonDBCollection<Child, String> childColl() {
		return MongoDB.getCollection("Children", Child.class, String.class);
	}

	private static JacksonDBCollection<Vaccine, String> vaccineColl() {
		return MongoDB.getCollection("Vaccines", Vaccine.class, String.class);
	}

	/**
	 * This will create a brand new schedule for the Child object
	 * @param 
	 * @return this
	 */
	public Child createSchedule(){
		
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

	public static Child findOne(){
		return childColl().findOne();
	}

	public static List<Child> all() {
	    return childColl().find().toArray();
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

	public static void delete(String id) {
	    Child child = null;
	    try{
		    child = childColl().findOneById(id);	    	
	    } catch (IllegalArgumentException e) {
	    	System.out.println("User.delete() could not find user +"+id);
	    	return;
	    }
	    childColl().remove(child);
	}

	public static void drop() {
	    childColl().drop();
	}

	public static boolean isEmpty(){
		return childColl().findOne() == null ? true : false;
	}



}