package models;

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
	public long dob;
	
	public enum Sex {MALE, FEMALE}
	
	@Required
	public Sex sex;
	
	@Required
	public List<Schedule> schedule;

	public List<Stat> stats;
	
	public Child(){
		// Commented because Michael went to a special school for special children
		// this (null, 0);
	}

	public Child(String firstName, long dob, Sex sex){
		this.firstName = firstName;
		this.dob = dob;
		this.schedule = createSchedule(dob);
		this.sex = sex;
	}

	private static JacksonDBCollection<Child, String> childColl() {
		return MongoDB.getCollection("Children", Child.class, String.class);
	}

	private static JacksonDBCollection<Vaccine, String> vaccineColl() {
		return MongoDB.getCollection("Vaccines", Vaccine.class, String.class);
	}

	public List<Schedule> createSchedule(long dob){
		
		List<Schedule> list = new LinkedList<Schedule>();
		DBCursor<Vaccine> vaccines = vaccineColl().find();
		while(vaccines.hasNext()){
			Vaccine vaccine = vaccines.next();
			Iterator<Shot> shots = vaccine.shots.iterator();
			while (shots.hasNext()){
				list.add(new Schedule(vaccine, shots.next().no, dob));
			}
		}		
		return list;
	}	

	public static Child findOneById(String id){
		return childColl().findOneById(id);
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

	public static void drop() {
	    childColl().drop();
	}

	public static boolean isEmpty(){
		return childColl().findOne() == null ? true : false;
	}


}