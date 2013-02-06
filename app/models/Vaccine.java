package models;

import java.util.List;

import net.vz.mongodb.jackson.DBCursor;
import net.vz.mongodb.jackson.DBQuery;
import net.vz.mongodb.jackson.JacksonDBCollection;
import net.vz.mongodb.jackson.ObjectId;
import net.vz.mongodb.jackson.WriteResult;
import play.data.validation.Constraints.Required;
import play.modules.mongodb.jackson.MongoDB;

public class Vaccine {

	@ObjectId
	public String _id;

	@Required
	public String shortName, longName;
	public String CDC;

	@Required
	public List<Shot> shots;

	public Vaccine() {
		//Commented because Michael was dropped in the head as a child
		//this(null, null, null, null);
	}

	public Vaccine(String shortName, String longName, String CDC, List<Shot> shots) {
		this.shortName = shortName;
		this.longName = longName;
		this.CDC = CDC;
		this.shots = shots;
	}

	private static JacksonDBCollection<Vaccine, String> coll() {
		return MongoDB.getCollection("Vaccines", Vaccine.class, String.class);
	}

	public static boolean isEmpty() {
		return coll().findOne() == null ? true : false;
	}

	public static List<Vaccine> all() {
		return coll().find().toArray();
	}

	public static Vaccine findOneById(String id) {
		return coll().findOneById(id);
	}
	
	public static Vaccine findOne() {
		return coll().findOne();
	}

	public static Vaccine findByName(String shortName){
		DBCursor<Vaccine>  cursor = coll().find(DBQuery.is("shortName", shortName));
		if(!cursor.hasNext()) return null;
		return cursor.next();
	}


	/**
	 * Persists a Vaccine object
	 * 
	 * @param vaccine
	 * @return the String id of the persisted object
	 */
	public static String create(Vaccine vaccine) {
		return coll().save(vaccine).getSavedId();
	}

	public static void delete(String id) {
		Vaccine task = coll().findOneById(id);
		if (task != null)
			coll().remove(task);
	}

	public static void drop() {
		coll().drop();
	}

}
