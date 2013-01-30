package models;

import java.util.List;

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
		this(null, null, null, null);
	}

	public Vaccine(String shortName, String longName, String CDC,
			List<Shot> shots) {
		this.shortName = shortName;
		this.longName = longName;
		this.CDC = CDC;
		this.shots = shots;
	}

	private static JacksonDBCollection<Vaccine, String> collection() {
		return MongoDB.getCollection("Vaccines", Vaccine.class, String.class);
	}

	public static boolean isEmpty() {
		return collection().findOne() == null ? true : false;
	}

	public static List<Vaccine> all() {
		return collection().find().toArray();
	}

	public static Vaccine findOne(String id) {
		return collection().findOneById(id);
	}

	/**
	 * Persists a Vaccine object
	 * 
	 * @param vaccine
	 * @return the String id of the persisted object
	 */
	public static String create(Vaccine vaccine) {
		return collection().save(vaccine).getSavedId();
	}

	public static void delete(String id) {
		Vaccine task = collection().findOneById(id);
		if (task != null)
			collection().remove(task);
	}

	public static void drop() {
		collection().drop();
	}

}
