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

	@Required
	public List<Schedule> schedule;
	
	public Child(){
		this (null, 0);
	}
	
	public Child(String firstName, long dob){
		this.firstName = firstName;
		this.dob = dob;
		this.schedule = createSchedule(dob);
	}
	
	private static JacksonDBCollection<Child, String> childColl() {
		return MongoDB.getCollection("Children", Child.class, String.class);
	}

	private static JacksonDBCollection<Vaccine, String> vaccineColl() {
		return MongoDB.getCollection("Vaccines", Vaccine.class, String.class);
	}

//	This method works in creating schedule objects attached to the child object and I can see them in command prompt
//	but when I run the unit test and try to call
	
	
/*	public static List<Schedule> createSchedule(long dob){
		List<Schedule> list = new LinkedList<Schedule>();
		DBCursor<Vaccine> vaccines = vaccineColl().find();
		while(vaccines.hasNext()){
			Vaccine vaccine = vaccines.next();
			Iterator<Shot> shots = vaccine.shots.iterator();
			while (shots.hasNext()){
				list.add(new Schedule(vaccine, shots.next().getNo(), dob));
			}
		}		
		return list;
	}
*/		
	
	public static List<Schedule> createSchedule(long dob){
		List<Schedule> list = new LinkedList<Schedule>();
		list.add(new Schedule(vaccineColl().findOne(), 1, dob));
		return list;
	}	
	
	
	public static Child findOne(String id){
		return childColl().findOneById(id);
	}
	
	public static List<Child> all() {
	    return childColl().find().toArray();
	}
	
	public static String create(Child child) {
	    return childColl().save(child).getSavedId();
	}

	public static void drop() {
	    childColl().drop();
	}
	
	public static boolean isEmpty(){
		return childColl().findOne() == null ? true : false;
	}


}
