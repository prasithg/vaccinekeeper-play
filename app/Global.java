import play.*;
import play.libs.*;

import java.util.*;

import models.*;

public class Global extends GlobalSettings {
    
    public void onStart(Application app) {
        InitialData.insert(app);
    }
    
//  Why is this executed as a static class??
    static class InitialData {
        
        @SuppressWarnings("unchecked")
		public static void insert(Application app) {

            Map<String,List<Object>> all = (Map<String,List<Object>>)Yaml.load("initial-data.yml");
        	
//          TODO Remove collection drops in production
            Vaccine.drop();
            if(Vaccine.all().size() == 0 ) {
                Iterator<Object> vaccines = all.get("vaccines").iterator();
                while(vaccines.hasNext())
                	Vaccine.create((Vaccine) vaccines.next());
            }
            
            User.drop();
            List<Object> users = all.get("users");
    		
            User user1 = (User)users.get(0);
    		user1 = User.create(user1);
    		
    		User user2 = (User)users.get(1);
    		user2 = User.create(user2);
            
            Child.drop();
            List<Object> children = all.get("children");

            Child child = (Child) children.get(0);
        	child.userId=user1._id;
        	Child.create(child.createSchedule());
        	
            child = (Child) children.get(1);
        	child.userId=user1._id;
        	Child.create(child.createSchedule());
        	
            child = (Child) children.get(2);
         	child.userId=user2._id;
         	Child.create(child.createSchedule());
         	
            child = (Child) children.get(3);
         	child.userId=user2._id;
         	Child.create(child.createSchedule());
            		
        }
	}
}