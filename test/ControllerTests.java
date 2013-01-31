import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.*;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import models.Child;
import models.Family;
import models.User;

import org.codehaus.jackson.JsonNode;
import org.junit.Test;

import play.mvc.Content;
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
					children.add(Child.findOneById(childIds.next()));
				}
				Family family = new Family(user, children);
				families.add(family);
				
				Content html = views.html.index.render(families);
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

	//Test post
	@Test
	public void callRegisterUser(){
		running(fakeApplication(), new Runnable(){
			@Override
			public void run() {
				JsonNode node = play.libs.Json.parse("{	\"userNameEmail\":\"steve@apple.com\"," +
						"								\"password\":\"password\"}");
				
				Result result = routeAndCall(fakeRequest(POST, "/register")
					.withHeader("Content-Type", "application/json")
					.withJsonBody(node));

				//At the moment, we're redirecting, so the content is empty
				assertThat(contentAsString(result)).isEmpty();
			}
		});
	}
	
	

	

}
