package controllers;

import models.Vaccine;
import play.mvc.Controller;
import play.mvc.Result;

public class Vaccines extends Controller {

	public static Result genericSchedule() {
		return ok(play.libs.Json.toJson(Vaccine.all()));
	}
	
}