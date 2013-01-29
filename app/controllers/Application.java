package controllers;

import java.util.List;

import models.Vaccine;
import play.mvc.Controller;
import play.mvc.Result;

public class Application extends Controller {

	public static Result index() {
		return ok(views.html.index.render("For the children!"));
	}

	public static Result genericSchedule() {
		return ok(play.libs.Json.toJson(getGenericSchedule()));
	}

	public static List<Vaccine> getGenericSchedule() {
		return Vaccine.all();
	}

}