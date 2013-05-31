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

public class Tests {

	//Test router
	@Test
	public void testHomeRoute(){
		Result result = routeAndCall(fakeRequest(GET, "/"));
		assertThat(result).isNotNull();
	}

	//TODO: Remove template test until we need it
//	@Test
	public void renderIndex() {
		running(fakeApplication(), new Runnable(){
			@Override
			public void run() {

//				Content html = views.html.index.render("test");
//				assertThat(html.body().indexOf(child.firstName)).isNotEqualTo(-1);
//				assertThat(contentType(html)).isEqualTo("text/html");
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


}
