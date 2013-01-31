package models;

import java.util.List;

/**
 * Convenience class for sending a family through the controller
 * @author Michael
 */
public class Family {
	public User user;
	public List<Child> children;
	
	public Family(User user, List<Child> children){
		this.user = user;
		this.children = children;
	}
}
