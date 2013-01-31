import static org.fest.assertions.Assertions.assertThat;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.callAction;
import static play.test.Helpers.charset;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.contentType;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.fakeRequest;
import static play.test.Helpers.routeAndCall;
import static play.test.Helpers.running;
import static play.test.Helpers.status;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import models.Child;
import models.Family;
import models.User;

import org.junit.Test;

import play.mvc.Content;
import play.mvc.Result;

public class ControllerTests {

	
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
					children.add(Child.findOne(childIds.next()));
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
				assertThat(play.libs.Json.parse(contentAsString(result)).get(0).get("shortName").toString().replace("\"", "")).isEqualTo("Hep B");
			}
		});
	}

	//Test router
	@Test
	public void testGenericScheduleRoute(){		
		running(fakeApplication(), new Runnable(){
			@Override
			public void run() {
				Result result = routeAndCall(fakeRequest(GET, "/genericSchedule"));
				assertThat(result).isNotNull();
			}
		});
	}


	

}
