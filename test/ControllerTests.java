import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.*;


import org.junit.Test;

import play.mvc.Result;

public class ControllerTests {

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

	@Test
	public void callGenericSchedule(){
		running(fakeApplication(), new Runnable(){
			@Override
			public void run() {
//				Content html = views.html.index.render("test");
				Result result = callAction(controllers.routes.ref.Application.genericSchedule());
				assertThat(status(result)).isEqualTo(OK);
				assertThat(contentType(result)).isEqualTo("application/json");
				assertThat(charset(result)).isEqualTo("utf-8");
			}
		});
	}



}
