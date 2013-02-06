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
        	
            if(Vaccine.findOne() == null || User.findOne() == null) {
                
                Map<String,List<Object>> all = (Map<String,List<Object>>)Yaml.load("initial-data.yml");

                Iterator<Object> vaccines = all.get("vaccines").iterator();
                while(vaccines.hasNext()){
                	Vaccine.create((Vaccine) vaccines.next());
                }
                
                
                
//                Ebean.save(all.get("users"));

                
            }
        }
        
    }
    
}