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
            
            Child.drop();
            List<String> childIds = new LinkedList<String>();
            if(Child.all().size() == 0 ){
                Iterator<Object> children = all.get("children").iterator();
            	while(children.hasNext()){
            		Child child = Child.create(((Child)children.next()).createSchedule());
            		childIds.add(child._id);
            	}
            }
            
            User.drop();
            if(User.all().size() == 0 ){
                Iterator<Object> users = all.get("users").iterator();
            	while(users.hasNext()){
            		User user = (User)users.next();
            		List<String> list = new LinkedList<String>();
            		list.add(childIds.get(0));
            		user.childIds= list;
            		User.create(user);
            	}
            }
        }
        
    }
    
}