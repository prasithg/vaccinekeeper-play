package controllers;
import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.*;
import static play.test.Helpers.*;
import org.apache.commons.lang.RandomStringUtils;

import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import models.Child;
import models.Family;
import models.Schedule;
import models.User;

import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import play.mvc.Content;
import play.mvc.Http.Status;
import play.mvc.Result;

public class ControllerTests {

	//Test router
	@Test
	public void testHomeRoute(){
		Result result = routeAndCall(fakeRequest(GET, "/"));
		assertThat(result).isNotNull();
	}

	//Test a template
	@Test
	public void renderIndex() {
		running(fakeApplication(), new Runnable(){
			@Override
			public void run() {

				User user = User.findOne();
				List<Family> families = new LinkedList<Family>();
				List<Child> children = new LinkedList<Child>();
				
				Iterator<String> childIds = user.childIds.iterator();
				while(childIds.hasNext()){
					Child child = Child.findOneById(childIds.next());
					if(child!=null) children.add(child);
				}
				Family family = new Family(user, children);
				families.add(family);
				
				Content html = views.html.index.render(families);
				assertThat(html.body().indexOf("Adelaide")).isNotEqualTo(-1);
				assertThat(contentType(html)).isEqualTo("text/html");
			}
		});
	}

	//Test controller and test JSON content
	@Test
	public void callGenericSchedule(){
		running(fakeApplication(), new Runnable(){
			@Override
			public void run() {
//				Content html = views.html.index.render("test");
				Result result = callAction(controllers.routes.ref.Vaccines.genericSchedule());
				assertThat(status(result)).isEqualTo(OK);
				assertThat(contentType(result)).isEqualTo("application/json");
				assertThat(charset(result)).isEqualTo("utf-8");
				
//				JSON test gives me an error about dispatchers
				assertThat(play.libs.Json.parse(contentAsString(result)).get(0).get("shortName").asText()).isEqualTo("Hep B");
			}
		});
	}

	//Test getChild
	@Test
	public void callGetChild(){
		running(fakeApplication(), new Runnable(){
			@Override
			public void run() {
				Child child = Child.findOne();
				Result result = callAction(controllers.routes.ref.Users.getChild(child._id));
				assertThat(play.libs.Json.parse(contentAsString(result)).get("firstName").asText()).isEqualTo(child.firstName);
				
			}
		});
	}

	//Test register user routing
	@Test
	public void callRegisterUser(){
		running(fakeApplication(), new Runnable(){
			@Override
			public void run() {
				
				User user = User.findByUserName("bill.gates@microsoft.com");
				if(user!= null) User.delete(user._id);
				
				JsonNode node = play.libs.Json.parse("{	\"userNameEmail\":\"bill.gates@microsoft.com\"," +
						"								\"password\":\"password\"}");
				
				Result result = routeAndCall(fakeRequest(POST, "/register")
					.withHeader("Content-Type", "application/json")
					.withJsonBody(node));

				assertThat(status(result)).isEqualTo(Status.SEE_OTHER);

				node = play.libs.Json.parse("{	\"userNameEmail\":\"bill.gates@microsoft.com\"," +
						"								\"password\":\"password\"}");
				
				result = routeAndCall(fakeRequest(POST, "/register")
					.withHeader("Content-Type", "application/json")
					.withJsonBody(node));

				assertThat(status(result)).isEqualTo(Status.NOT_FOUND);

				
			}
		});
	}
	
	@Test
	public void callDeleteUser(){
		running(fakeApplication(), new Runnable(){
			@Override
			public void run() {
				
				User user = User.findByUserName("prasith@vaccinekeeper.com");
				
				if(user==null){
					User pGovin = new User("prasith@vaccinekeeper.com", "password");
					pGovin = User.create(pGovin);
					
					Calendar dob = Calendar.getInstance();
					dob.set(2009, 8, 25);
					Child samina = new Child("Samina", dob.getTimeInMillis(), Child.Sex.FEMALE);
					samina = Child.create(samina);
					
					User.addChild(pGovin._id, samina._id);
				}
				
				String childId = user.childIds.iterator().next();			
				JsonNode node = play.libs.Json.parse("{	\"userNameEmail\":\""+user.userNameEmail+"\"," +
						"								\"password\":\""+user.password+"\"}");
				
				routeAndCall(fakeRequest(POST, "/delete")
					.withHeader("Content-Type", "application/json")
					.withJsonBody(node));

				user = User.findByUserName("prasith@vaccinekeeper.com");
				
				assertThat(user).isNull();
				assertThat(Child.findOneById(childId)).isNull();

			}
		});
	}
	
	//Would be good to simply callAction on the controller but I'm not sure how to pass in variables with the current setup
	@Test
	public void callUpdateSchedule(){
		running(fakeApplication(), new Runnable(){
			@Override
			public void run() {
				Child child = Child.findOne();
				
				int index = 5;
				String rString = RandomStringUtils.randomAlphabetic(5);
				long lastMod = child.schedule.get(index).lastModified+10;

				JsonNode node = play.libs.Json.parse(	"{	\"childId\":\""+child._id+"\"," +
														"\"schedule\":{\"shortName\":\"RV\", \"shot\":3, " +
														"\"cancelled\":\"false\",\"complete\":\"true\", \"comment\":\""+rString+"\"," +
														"\"lastModified\": "+lastMod+",\"scheduledDate\":1000}}");				
				routeAndCall(fakeRequest(POST, "/schedule")
					.withHeader("Content-Type", "application/json")
					.withJsonBody(node));

				child = Child.findOneById(child._id);
				
				assertThat(child.schedule.get(index).comment).isEqualTo(rString);
			}
		});
	}
	

	

}
