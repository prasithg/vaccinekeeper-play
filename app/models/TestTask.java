package models;

import java.util.List;

import net.vz.mongodb.jackson.Id;
import net.vz.mongodb.jackson.JacksonDBCollection;
import net.vz.mongodb.jackson.ObjectId;
import net.vz.mongodb.jackson.WriteResult;
import play.data.validation.Constraints.Required;
import play.modules.mongodb.jackson.MongoDB;

//@Entity
//public class Task extends Model {

public class TestTask {

	// @Id
	// public Long id;

	@Id
	@ObjectId
	public String id;

	@Required
	public String label;

	// public static Finder<Long, Task> find = new Finder(Long.class,
	// Task.class);
	// private static JacksonDBCollection<Task, String> coll =
	// MongoDB.getCollection("tasks", Task.class, String.class);

	private static JacksonDBCollection<TestTask, String> coll() {
		return MongoDB.getCollection("Test", TestTask.class, String.class);
	}

	public static boolean isEmpty() {
		return coll().findOne() == null ? true : false;
	}

	public static List<TestTask> all() {
		// return find.all();
		return coll().find().toArray();
	}

	public static TestTask findOne(String id) {
		return coll().findOneById(id);
	}

	public static String create(TestTask task) {
		// task.save();
		WriteResult<TestTask, String> result = coll().save(task);
		return result.getSavedId();
	}

	public static void delete(String id) {
		// find.ref(id).delete();
		TestTask task = coll().findOneById(id);
		if (task != null)
			coll().remove(task);
	}

	public static void drop() {
		coll().drop();
	}

}
