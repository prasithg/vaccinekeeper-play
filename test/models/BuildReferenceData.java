package models;
import static org.fest.assertions.Assertions.assertThat;
import static play.test.Helpers.fakeApplication;
import static play.test.Helpers.running;

import play.Logger;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import models.Child;
import models.Shot;
import models.TestTask;
import models.User;
import models.Vaccine;

import org.junit.Test;

public class BuildReferenceData {

//	@Test
	public void testCleanAndSave() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				// Drop data
				TestTask.drop();
				assertThat(TestTask.isEmpty()).isTrue();

				// Save a task
				TestTask task = new TestTask();
				String label = "Check if Generic Schedule collection exists";
				task.label = label;
				String id = TestTask.create(task);

				// Retrieve and check
				task = TestTask.findOne(id);
				assertThat(task.label).isEqualTo(label);
			}
		});
	}

//	@Test
	public void buildGenericSchedule() {
		running(fakeApplication(), new Runnable() {

			@Override
			public void run() {
				// Drop data
				Vaccine.drop();
				assertThat(Vaccine.isEmpty()).isTrue();

				// Build it
				List<Shot> shots = new LinkedList<Shot>();
				shots.add(new Shot(1, 0, 0));
				shots.add(new Shot(2, 1, 2));
				shots.add(new Shot(3, 6, 18));
				String hepBId = Vaccine.create(new Vaccine("Hep B",
						"Hepatitis B", null, shots));

				shots = new LinkedList<Shot>();
				shots.add(new Shot(1, 2, 2));
				shots.add(new Shot(2, 4, 4));
				shots.add(new Shot(3, 6, 6));
				Vaccine.create(new Vaccine("RV", "Rotavirus", null, shots));

				shots = new LinkedList<Shot>();
				shots.add(new Shot(1, 2, 2));
				shots.add(new Shot(2, 4, 4));
				shots.add(new Shot(3, 6, 6));
				shots.add(new Shot(4, 15, 18));
				shots.add(new Shot(5, 48, 60));
				String dTaPId = Vaccine
						.create(new Vaccine("DTaP",
								"Diphtheria, Tetanus, acellular Pertussis",
								null, shots));

				shots = new LinkedList<Shot>();
				shots.add(new Shot(1, 2, 2));
				shots.add(new Shot(2, 4, 4));
				shots.add(new Shot(3, 6, 6));
				shots.add(new Shot(4, 12, 15));
				Vaccine.create(new Vaccine("Hib",
						"Haemophilus influenzae type b", null, shots));

				shots = new LinkedList<Shot>();
				shots.add(new Shot(1, 2, 2));
				shots.add(new Shot(2, 4, 4));
				shots.add(new Shot(3, 6, 6));
				shots.add(new Shot(4, 12, 15));
				Vaccine.create(new Vaccine("PCV", "Pneumococcal Conjugate",
						null, shots));

				shots = new LinkedList<Shot>();
				shots.add(new Shot(1, 48, 60));
				Vaccine.create(new Vaccine("PSSV",
						"Pneumococcal Polysascharide", null, shots));

				shots = new LinkedList<Shot>();
				shots.add(new Shot(1, 2, 2));
				shots.add(new Shot(2, 4, 4));
				shots.add(new Shot(3, 6, 18));
				shots.add(new Shot(4, 48, 60));
				Vaccine.create(new Vaccine("IPV", "Inactivated PolioVirus",
						null, shots));

				shots = new LinkedList<Shot>();
				shots.add(new Shot(1, 6, 17));
				shots.add(new Shot(2, 18, 29));
				shots.add(new Shot(3, 30, 41));
				shots.add(new Shot(4, 42, 53));
				shots.add(new Shot(5, 54, 65));
				shots.add(new Shot(6, 66, 77));
				Vaccine.create(new Vaccine("Flu", "Influenza", null, shots));

				shots = new LinkedList<Shot>();
				shots.add(new Shot(1, 12, 15));
				shots.add(new Shot(2, 48, 60));
				Vaccine.create(new Vaccine("MMR", "Measles, Mumps, Rubella",
						null, shots));

				shots = new LinkedList<Shot>();
				shots.add(new Shot(1, 12, 15));
				shots.add(new Shot(2, 48, 60));
				Vaccine.create(new Vaccine("Var", "Varicella", null, shots));

				shots = new LinkedList<Shot>();
				shots.add(new Shot(1, 12, 23));
				shots.add(new Shot(2, 24, 60));
				String hepAId = Vaccine.create(new Vaccine("Hep A",
						"Hepatitis A", null, shots));

				shots = new LinkedList<Shot>();
				shots.add(new Shot(1, 9, 60));
				Vaccine.create(new Vaccine("MCV4", "MeningoCoccal", null, shots));

				// Retrieve and check
				Vaccine hepB = Vaccine.findOneById(hepBId);
				assertThat(hepB.shortName).isEqualTo("Hep B");
				assertThat(hepB.longName).isEqualTo("Hepatitis B");
				assertThat(hepB.shots.size()).isEqualTo(3);
				assertThat(hepB.shots.get(2).end).isEqualTo(18);

				Vaccine dTaP = Vaccine.findOneById(dTaPId);
				assertThat(dTaP.shortName).isEqualTo("DTaP");
				assertThat(dTaP.longName).isEqualTo(
						"Diphtheria, Tetanus, acellular Pertussis");
				assertThat(dTaP.shots.size()).isEqualTo(5);
				assertThat(dTaP.shots.get(3).start).isEqualTo(15);

				Vaccine hepA = Vaccine.findOneById(hepAId);
				assertThat(hepA.shortName).isEqualTo("Hep A");
				assertThat(hepA.longName).isEqualTo("Hepatitis A");
				assertThat(hepA.shots.size()).isEqualTo(2);
				assertThat(hepA.shots.get(1).end).isEqualTo(60);

			}
		});
	}
	
	
//	@Test
	public void buildUsersAndChildren(){
		running(fakeApplication(), new Runnable(){

			@Override
			public void run() {
				
				//Drop data
				User.drop();
				assertThat(User.isEmpty()).isTrue();
				Child.drop();
				assertThat(Child.isEmpty()).isTrue();

				//Create and add user
				User mJaniak = new User("michael@vaccinekeeper.com","password");
				mJaniak = User.create(mJaniak);
				
				//Create and save children
				Calendar dob = Calendar.getInstance();
				
				dob.set(2012, 0, 25);
				Child adelaide = new Child("Adelaide", dob.getTimeInMillis(), Child.Sex.FEMALE);
				adelaide = Child.create(adelaide);
				
				dob.set(2009, 2, 1);
				Child douglas = new Child("Douglas", dob.getTimeInMillis(), Child.Sex.MALE);
				douglas = Child.create(douglas);
				
				//Reference children to user and refresh user
				User.addChild(mJaniak._id, adelaide._id);
				User.addChild(mJaniak._id, douglas._id);
				mJaniak = User.findOneById(mJaniak._id);

				
				//Create and add user
				User pGovin = new User("prasith@vaccinekeeper.com", "password");
				pGovin = User.create(pGovin);
				
				//Create and save children			
				dob.set(2009, 8, 25);
				Child samina = new Child("Samina", dob.getTimeInMillis(), Child.Sex.FEMALE);
				samina = Child.create(samina);
				
				dob.set(2010, 8, 14);
				Child barry = new Child("Barry", dob.getTimeInMillis(), Child.Sex.MALE);
				barry = Child.create(barry);
				
				//Reference children to user
				User.addChild(pGovin._id, samina._id);
				User.addChild(pGovin._id, barry._id);
				pGovin = User.findOneById(pGovin._id);

				
				//Test children
				assertThat(mJaniak.userNameEmail).isEqualTo("michael@vaccinekeeper.com");
				assertThat(mJaniak.childIds.size()).isEqualTo(2);
				assertThat(mJaniak.childIds.get(1)).isEqualTo(douglas._id);
				
				Child douglas2 = Child.findOneById(douglas._id);
				assertThat(douglas2.firstName).isEqualTo("Douglas");

			}
		});
	}

//	@Test
	public void testUserNameSearch(){
		running(fakeApplication(), new Runnable(){

			@Override
			public void run() {
				
				User user = User.findByName("prasith@vaccinekeeper.com");
				if(user==null){
					user = new User("prasith@vaccinekeeper.com", "password");
					user = User.create(user);					
				}
				
				user = User.findByName("prasith@vaccinekeeper.com");
				assertThat(user).isNotNull();
				
				user = User.findByName("dude madness");
				assertThat(user).isNull();

			}
		});
	}

	

}
