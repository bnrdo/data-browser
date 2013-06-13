package com.bnrdo.databrowser;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.bnrdo.databrowser.Constants.SQL_TYPE;
import com.bnrdo.databrowser.DataBrowser;
import com.bnrdo.databrowser.domain.Person;
import com.bnrdo.databrowser.format.PersonFormat;

public class DataBrowserSampleUsage2 {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					//UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
				} catch (Exception e){
					e.printStackTrace();
				}
				
				DataBrowserSampleUsage2 creator = new DataBrowserSampleUsage2();

				JFrame frame = new JFrame("Demo");
				frame.getContentPane().setLayout(new BorderLayout());
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().add(creator.createDataBrowser(), BorderLayout.CENTER);
				frame.pack();
				frame.setVisible(true);
			}
		});
	}
	
	private DataBrowser<Person> createDataBrowser() {
		final DataBrowser<Person> dbrowse = new DataBrowser<Person>();
		//final List<Person> source =  generateRandomSource();
		final ColumnInfoMap colInfoMap = new ColumnInfoMap();
		
		colInfoMap.putColumnName(0, "First Name");
		colInfoMap.putColumnName(1, "Last Name");
		colInfoMap.putColumnName(2, "Birthday");
		colInfoMap.putColumnName(3, "Age");
		colInfoMap.putColumnName(4, "Occupation");
		
		colInfoMap.putPropertyName(0, "firstName");
		colInfoMap.putPropertyName(1, "lastName");
		colInfoMap.putPropertyName(2, "birthDay");
		colInfoMap.putPropertyName(3, "age");
		colInfoMap.putPropertyName(4, "occupation");
		
		colInfoMap.putPropertyType(0, SQL_TYPE.STRING);
		colInfoMap.putPropertyType(1, SQL_TYPE.STRING);
		colInfoMap.putPropertyType(2, SQL_TYPE.TIMESTAMP);
		colInfoMap.putPropertyType(3, SQL_TYPE.INTEGER);
		colInfoMap.putPropertyType(4, SQL_TYPE.STRING);
		
		Connection con = null;
		Statement stmt = null;
		try {
			Class.forName("org.hsqldb.jdbcDriver");
			con = DriverManager.getConnection("jdbc:hsqldb:mem:data-browser", "sa", "");
			//con = DriverManager.getConnection("jdbc:hsqldb:file:C:\\Users\\ut1p98\\Desktop\\db\\data-browser", "sa", "");
			//con = DriverManager.getConnection("jdbc:hsqldb:file:C:\\Users\\ut1p98\\Desktop\\db\\data-browser", "sa", "");
			stmt = con.createStatement();
			// table name = data_browser_persist
			stmt.execute("DROP TABLE IF EXISTS employee;");
			stmt.execute("SET IGNORECASE TRUE;");
			stmt.execute("CREATE CACHED TABLE employee (" +
					"id integer not null primary key," +
					"firstName varchar(256) not null," +
					"lastName varchar(256) not null," +
					"birthday timestamp," +
					"age integer," +
					"occupation varchar(256) not null)");
			String qry = "INSERT INTO employee VALUES(?,?,?,?,?,?)";
			PreparedStatement ps = con.prepareStatement(qry);
			for(int i = 0; i <1000; i++){
				ps.setInt(1, i);
				ps.setString(2, getRandomFirstName());
				ps.setString(3, getRandomLastName());
				ps.setDate(4, getRandomDate());
				ps.setInt(5, getRandomAge());
				ps.setString(6, getRandomOccupation());
				ps.addBatch();
				if(i%1000 == 0 || i == 999){
					ps.executeBatch();	
				}
			}
			
			//stmt.execute(DBroUtil.translateColInfoMapToCreateDbQuery(colInfoMap));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}  catch(SQLException e){
			e.printStackTrace();
		}
		
		dbrowse.setColInfoMap(colInfoMap);
		dbrowse.setTableDataSourceFormat(new PersonFormat());
		dbrowse.setDataTableSource(con, "employee");
		dbrowse.create();
		
		dbrowse.getTestButton().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//dbrowse.setDataTableSource(generateRandomSource());
				//dbrowse.create();
			}
		});
		
		return dbrowse;
	}
	
	private String getRandomFirstName(){
		String[] names = new String[]{"Mario",	"Petey",	"Anna",		"Paul",	"Anna",		"Gail",		"Paige",	"Bob",		"Walter",	"Nick",
									"Barb",		"Buck",		"Greta",	"Ira",	"Shonda",	"Brock",	"Maya",		"RickShea",	"Monty",	"Sal",
									"Sue",		"Cliff",	"Barb",		"Terry","Cory",		"Robin",	"Jimmy",	"Barry",	"Wilma",	"Buster",
									"Poppa",	"Zack",		"Don",		"Saul",	"Peter",	"Hal",		"Moe",		"Graham",	"Tom",		"Al",	
									"Polly",	"Holly",	"Frank",	"Cam",	"Pat",		"Tara",		"Barry",	"Phil ",	"Bud",	 	"Phil ",
									"Will",		"Donatella","Juan",		"Stew",	"Anna",		"Bill",		"Curt",		"Max",		"Minnie",	"Bill",
									"Hap",		"Matt",		"Polly",	"Tara",	"Ed",		"Gerry",	"Kerry",	"Midge",	"Gabe",		"Mary",		
									"Dan",		"Jim",		"Angie",	"Ella",	"Sal",		"Bart",		"Artie",	"Hans",		"Marge",	"Hugh",
									"Gene",		"Ty",		"Manuel",	"Lynn",	"Claire",	"Peg",		"Jack",		"Marty",	"Ash",		"Olive",
									"Gene",		"Tom",		"Doug",		"Sharon","Beau",	"Serj",		"Marcus",	"Warren",	"Bud",		"Barney",
									"Marion",	"Eric",		"Mal",		"Ed",	"Rick",		"Paul",		"Ben",		"Kat",		"Justin",	"Louie",
									"Aaron",	"Ty",		"Anne",		"Barry","John",		"Joe",		"Mary",		"Marge",	"Frank",	"Bill",
									"Ray",		"Adam",		"Lewis", 	"Matt",	"Sue",		"Chris",	"Doug",		"Mason",	"Sil",		"Cal",	
									"Sara",		"Al",		"Marv",		"Evan",	"Terry",	"Mort",		"Mark",		"Ken",		"Louis",	"Colin",
									"Fred",		"Al",		"Penny",	"Reed",	"Chip",		"Matt",		"Jack",		"Mike",		"Lou",		"Faye",
									"Javy",		"Tom",		"Sam",		"Phil",	"Sam",		"Mary",		"Ray",		"Curtis",	"Holland",	"Helen",
									"Eddy",		"Alejandro","Sir",		"Elle", "Yu",		"Ellis",	"Anna",		"Sara",		"Penny",	"Phil"};
		Random r = new Random();
		int ran = r.nextInt(170);
		return names[ran];
	}
		
	private String getRandomLastName(){
		String[] names = new String[]{"Speedwagon","Cruiser","Sthesia","Molive","Mull","Forcewind","Turner","Frapples","Melon","R.Bocker",
			"Ackue","Kinnear","Life","Membrit","Leer","Lee","Didas","O'Shea","Sariya","Carlo","Monella","Vaneer",
			"Hanger","Dwyer","Aki","Ander","Banks","Changa","Wine","Mumduya","Hyman","Cherry","Lee","Stairs","T.Balls",
			"Pants","Appeno","Matic","Fugga","Cracker","Foolery","Dente","Wiser","Tech","Graham","N.Stein","L.Toe","Agonia",
			"Zona","Cade","Anthropist","Gardens","Harmonic","Ficial","Power","Nobatti","Annatoo","Gots","Rexia","Emia","N.Call",
			"Emum","Mum","Yerds","E.Birthday","Innae","Science","Misu","U.Cation","Atric","Oaky","Itz","Lackmen","Christmas",
			"Druff","Nasium","O.Plasty","Vator","Vidge","Ender","Choke","Olo","Arin","Briss","Poole","Tanic","Labor","Guini",
			"Voyant","Leg","E.Sack","Graw","Wednesday","Yu","Jacket","Atoe","Out","Needles","Tie","Protector","Down","Peace","Jet",
			"Cull","Gaze","Shun","Practice","Itorial","Shaw","Issy","Effit","E.Gory","Case","Z.Ana","Ottix","Ballgame","Fibbiyon",
			"Cuda","Withawind","Thyme","Goround","Arita","Senbeans","Dabear","Zindaroof","Zapple","N.Clark","Schtick","Shee","P.Bacon",
			"LeeDuckling","Protesters","Antro","Orie","Bellum","Acart","Ellis","Shlee","Bull","Ission","Ette","Tucky","Ville","Oscopy",
			"Attchini","Fredo","Tration","Iculous","Zinsalsa","Uhrafact","Dup","Roscope","Sinclark","Daway","Cado","Ollie","Buca",
			"Anderer","Owen","Achi","Cyst","E.Flush","Oats","Highwater","Kitt","Toesacks","KimScision","Bowdrop","Tube","Dee","Lytics",
			"Bellum","Trate","Erup","Side","Nara","Donalds","Alert","Tory","Pin","Inate","Miliation","Mingle","Soponatime","Sinferno",
			"Zupp","Yevo","Thetip","Itis","Sbook","R.Pigeon","Slurs","Pryder","Slaw","Ernity","Utant","Warm","Tee","Fication","Itician",
			"Utant","Thegrass","Sell","Schtape","Pendant","Furter","Dalive","Adella","Diation","Adamia","Moan","Retical","Torial","O'Nayse",
			"Ruse","thra","Doubt","Larm","Body","Ami","Derr","Derr","O'Scopy","Anoma","Up","Zing","Key","Gineer","Rib","Ficial","Inabottle",
			"Therapy","Stration","Ow","Dorporal","Tenant","Knack","Whack","Sandwich","Slavia","Spacemuseum","Atricks","Delion","Torial",
			"Q.Later","Trification","Nile","Volver","Strone","Wind","Samic","Gret","Tick","Diver","E.Riser","Edoh","Youfelct","Carnation",
			"Misunday","P.Cream","Metric","Innet","Mello","Petty","Adictorian","Tania","Amole"};
		Random r = new Random();
		int ran = r.nextInt(266);
		return names[ran];
	}
	
	private java.sql.Date getRandomDate(){
		Date date = new Date();
		return new java.sql.Date(date.getTime());
	}
	
	private int getRandomAge(){
		Random r = new Random();
		int retVal = r.nextInt(20);
		return retVal + 20;
	}
	
	private String getRandomOccupation(){
		String[] occupations = new String[]{"Aboriginal and Torres Strait Islander Health Worker",  "Accommodation and Hospitality Managers nec",  "Accountant (General)",  "Actor",  "Actors, Dancers and Other Entertainers",  "Actuary",  "Acupuncturist",  "Advertising and Public Relations",  "Advertising Manager",  "Advertising Specialist",  "Aeronautical Engineer",  "Aeroplane Pilot",  "Agricultural Consultant",  "Agricultural Engineer",  "Agricultural Scientist",  "Agricultural Technician",  "Air Traffic Controller",  "Air Transport Professionals NEC",  "Airconditioning and Mechanical Services Plumber",  "Airconditioning and Refrigeration Mechanic",  "Aircraft Maintenance Engineer (Avionics)",  "Aircraft Maintenance Engineer (Mechanical)",  "Aircraft Maintenance Engineer (Structures)",  "Airconditioning and Refrigeration Mechanic",  "Ambulance Officer",  "Amusement Centre Manager",  "Anaesthetist",  "Analyst Programmer",  "Anatomist or Physiologist",  "Animal Attendants and Trainers nec",  "Antique Dealer",  "Apiarist",  "Apparel Cutter",  "Arborist",  "Architect",  "Architectural, Building and Surveying Technicians nec",  "Architectural Draftsperson",  "Archivist",  "Art Administrator or Manager",  "Art Director (Film, Television or Stage)",  "Art Teacher (Private Tuition)",  "Artistic Director",  "Auctioneer",  "Audiologist",  "Authors",  "Automotive Electrician",  "Baker",  "Barrister",  "Bed and Breakfast Operator",  "Beef Cattle Farmer",  "Betting Agency Manager",  "Binder and Finisher",  "Biochemist",  "Biomedical Engineer",  "Biotechnologist",  "Blacksmith",  "Boarding Kennel or Cattery Operator",  "Boat Builder and Repairer",  "Book or Script Editor",  "Botanist",  "Bricklayer",  "Educational Psychologist",  "Electorate Officer",  "Electrical Engineer",  "Electrical Engineering Draftsperson",  "Electrical Engineering Technician",  "Electrical Linesworker",  "Electrician (General)",  "Developer Programmer",  "Diagnostic and Interventional Radiologist",  "Diesel Motor Mechanic",  "Dietitian",  "Director (Film, Television, Radio or Stage)",  "Diver",  "Diversional Therapist",  "Diving Instructor (Open water)",  "Dog and Horse Racing Official",  "Conveyancer",  "Cook",  "Copywriter",  "Corporate General Manager",  "Corporate Treasurer",  "Cotton Grower",  "Counsellors NEC",  "Court Bailiff or Sheriff (Aus) / Court Collections Officer (NZ)",  "Crop Farmers Nec",  "Florist",  "Flower Grower",  "Flying Instructor",  "General Medical Practitioner",  "Geologist",  "Geophysicist",  "Geotechnical Engineer",  "Glazier",  "Horse Riding Coach or Instructor",  "Horse Trainer",  "Hospital Pharmacist",  "ICT Systems Test Engineer",  "Illustrator",  "Importer or Exporter",  "Industrial Designer",  "Jeweller",  "Jewellery Designer",  "Jockey",  "Joiner",  "Life Science Technician",  "Life Scientist",  "Life Scientists NEC",  "Lift Mechanic",  "Marine Biologist",  "Market Research Analyst",  "Marketing Specialist",  "Nurse Researcher",  "Nurseryperson",  "Nursing Clinical Director",  "Obstetrician and Gynaecologist",  "Occupational Health and Safety Adviser",  "Occupational Therapist",  "Office Manager",  "Ophthalmologist",  "Optical Dispenser",  "Optical Mechanic",  "Optometrist",  "Park Ranger",  "Parole or Probation Officer",  "Pastrycook",  "Patents Examiner",  "Pathologist",  "Quality Assurance Manager",  "Quantity Surveyor",  "Quarantine Officer",  "Registered Nurse (Mental Health)",  "Registered Nurse (Perioperative)",  "Registered Nurse (Surgical)",  "Registered Nurses NEC",  "Rehabilitation Counsellor",  "Renal Medicine Specialist",  "Research and Development Manager",  "Residential Care Officer",  "Secondary School Teacher",  "Senior Non-Commissioned Defence Force Member",  "Sheep Farmer",  "Sheetmetal Trades Worker",  "Ship Surveyor",  "Ship's Master",  "Ship's Officer",  "Shipwright",  "Television Equipment Operator",  "Television Journalist",  "Television Presenter",  "Tennis Coach",  "Textile, Clothing and Footwear Mechanic",  "Thoracic Medicine Specialist",  "Toolmaker",  "Traditional Chinese Medical Practitioner",  "Training and Development Professional",  "University Tutor",  "Upholsterer",  "Urban and Regional Planner",  "Urologist",  "Vehicle Trimmer",  "Veterinarian",  "Video Producer",  "Visual Arts and Crafts Professionals NEC",  "Wood Turner",  "Wool Buyer",  "Workplace Relations Adviser",  "Youth Worker",  "Zookeeper",  "Zoologist" };
		Random r = new Random();
		int ran = r.nextInt(170);
		return occupations[ran];
	}
}


