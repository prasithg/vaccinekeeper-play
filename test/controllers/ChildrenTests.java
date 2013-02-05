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

public class ChildrenTests {


	//Test getChild
	@Test
	public void callGetChild(){
		running(fakeApplication(), new Runnable(){
			@Override
			public void run() {
				Child child = Child.findOne();
				Result result = callAction(controllers.routes.ref.Children.getChild(child._id));
				assertThat(play.libs.Json.parse(contentAsString(result)).get("firstName").asText()).isEqualTo(child.firstName);
				
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

				JsonNode node = play.libs.Json.parse(	"{	\"_id\":\""+child._id+"\"," +
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
