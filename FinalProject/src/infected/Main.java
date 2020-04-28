package infected;
	
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javafx.event.ActionEvent; 
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;


public class Main extends Application {
	// Initialize objects / variables 
    Stage window;
    TextField nameInput;
    Label countryLabel, symptomLabel, countryInfo, virusName;
    ComboBox<String> symptomBox;
    ComboBox<Country>countryBox;
    ArrayList<Country> countries;
    ArrayList<Symptom> symptoms;
    Button startButton;
    HashMap<String, Symptom> symptomMap;
    /*
     * This method creates the stage and scene for the user to interact 
     * and will call to countryView
     */
	@Override
	public void start(Stage primaryStage) {
		try {
	        window = primaryStage;
	        //Set the title of the application window
	        window.setTitle("Virus Infection Game");
			TilePane root = new TilePane();
			
			//Call "readCountries()" to read over all the countries and place them in 
			//an arraylist
			countries = readCountries();
			
			//Create a hashmap that will be able to map all the symptom objects
			//Call "readSymptoms()" to read all the symptoms and place them in a hashmap
			symptomMap = new HashMap<String, Symptom>();
			readSymptoms();
			
			//Start Button
			startButton = new Button("Start Game");
			
			//---Button Action--- -> Action after pressing button takes you to next scene
			//////////////////////////////////////////////////////////////////////////////////////////
			startButton.setOnAction(e -> {
				//Create a virus with information on what the player inputted -> (Virus Name, Symptom, Country)
				Virus virus = new Virus(nameInput.getText(), symptomMap.get(symptomBox.getValue())); 
				
				//remove the symptom from the hashmap, so it cannot be chosen again
				symptomMap.remove(symptomBox.getValue()); 
				countryBox.getValue().addSick(1);
				
				//Goes to the "CountryView" class to create the next scene ------->
				CountryView view = new CountryView(new World(countries, virus), symptomMap);
				
				//Set the window height and width of next scene
				window.setHeight(820);
				window.setWidth(850);
				
				//Create 2nd scene
			    primaryStage.getScene().setRoot(view.getRootPane());
			});
			//////////////////////////////////////////////////////////////////////////////////////////
			
			//Create virus name label
			virusName = new Label("Enter virus name");
			
			//Textfield created with prompt text asking for name
			nameInput = new TextField();
			nameInput.setPromptText("Name");
			
			//Create symptom label for player to choose symptom and have an arraylist of symptom names
			symptomLabel = new Label("Choose the starting symptom");
			ArrayList<String> symptoms = new ArrayList<String>();
			
			// populate the combo box's array with the hashmap items
    		Set set = symptomMap.entrySet();
    		Iterator iterator = set.iterator();
    		while (iterator.hasNext())
    		{
    			//Grab all the keys (Symptom Names) and add them to the symptom arraylist
    			Map.Entry<String, Symptom> mentry = (Map.Entry<String, Symptom>) iterator.next();
				symptoms.add(mentry.getKey());
    		}
    		//Add all the symptoms into the combobox
			symptomBox = new ComboBox<String>(FXCollections.observableArrayList(symptoms));
			symptomBox.setValue(symptoms.get(0));
			
			//Create country label and grab all the country objects and put them in the country combobox
			countryLabel = new Label("Choose the starting country");
			countryBox = new ComboBox<Country>(FXCollections.observableArrayList(countries));
			
			//---Button Action--- -> When the user changes the combo box, it will update the info on the country
			/////////////////////////////////////
			countryBox.setOnAction(updateInfo);  
			/////////////////////////////////////
			
			//Create country label for the country info to display
			countryInfo = new Label("Country info:");
			
			//update the comboboxes to the first element in the arraylist in order to show the country info label
			countryBox.setValue(countries.get(0));
			
			//Create a vbox to align all the items vertically
			VBox vBox = new VBox(virusName, nameInput, symptomLabel, symptomBox, countryLabel, countryBox, countryInfo, startButton);
			vBox.setAlignment(Pos.CENTER);
			VBox.setMargin(nameInput, new Insets(10, 30, 10, 30));
			Scene scene = new Scene(vBox,300,400);
			
			// load the styling sheet
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			window.setScene(scene);
			window.show();
			
		} catch(Exception e) {  //CATCH any exception and display it on console if error
			e.printStackTrace();
		}
	}
	/*
	 * ---Button Action---
	 * When player presses a country, it will lead to this method where it will
	 * display all the info on the country picked
	 */
	EventHandler<ActionEvent> updateInfo = 
            new EventHandler<ActionEvent>() { 
      public void handle(ActionEvent e) 
      { 
    	  countryInfo.setText("Country info:\n" + countryBox.getValue().getInfo()); 
      } 
  }; 
	
	public static void main(String[] args) {
		launch(args);
	}
	
	
	/*
	 * Helper method to read all strings from a text file and return the strings from the list as a arraylist
	 */
	private static ArrayList<String> readNames(String fname)
	{
		ArrayList<String> names = new ArrayList<String>(); // Array list of all the names of the countries
        try (Scanner fileReader = new Scanner(new File(fname))){
            while (fileReader.hasNextLine()) // loop through the entire file
            {
                names.add(fileReader.nextLine());
            }
        }catch (FileNotFoundException e){	// CATCH the exception if the file for the countries is not found
                e.printStackTrace();
        }
        return names;
	}
	
	/*
	 * Helper method to create country objects using readNames helper method
	 */
	private static ArrayList<Country> readCountries() 
	{
		// create arraylist object, which will be returned later
		ArrayList<Country> countries = new ArrayList<Country>();
		ArrayList<String> names = readNames("countries.txt");
		
		/*
		 * this loop will loop through the names arraylist and will strip the string s by separating the commas 
		 * and then create a country object
		 */
		for (String s : names)
		{
			//This will strip off all the commas from every string and number
			//Strips the name off up to the next comma
			String name = s.substring(0, s.indexOf(','));
			
			//Strips the population number off up to the next comma
			s = s.substring(s.indexOf(',') + 1, s.length());
			int population = Integer.parseInt(s.substring(0, s.indexOf(',')));
			
			//Strips the string of whether its a hot or cold country
			s = s.substring(s.indexOf(',') + 1, s.length());
			Boolean hot = s.substring(0, s.indexOf(',')).equals("Hot");
			
			//Strips the wealth number off for the country
			s = s.substring(s.indexOf(',') + 1, s.length());
			int wealth = Integer.parseInt(s);
			
			//Create hot or cold countries depending whether "Boolean hot" is true or false 
			if (hot)
			{
				countries.add(new HotCountry(name, population, wealth));
			}
			else
			{
				countries.add(new ColdCountry(name, population, wealth));
			}
		}
		return countries;
	}
	
	/*
	 * this function is the same as the one from the main class, but modifies the hashmap 
	 */
	private void readSymptoms() 
	{
		ArrayList<String> names = readNames("symptoms.txt");
		/*
		 * this loop will loop through the names arraylist and will strip the string s by separating the commas
		 * and then create a symptom object
		 */
		for (String s : names)
		{
			//Strips the name off up to the next comma
			String name = s.substring(0, s.indexOf(','));
			
			//Strips the infectivity number off
			s = s.substring(s.indexOf(',') + 1, s.length());
			int infectivity = Integer.parseInt(s.substring(0, s.indexOf(',')));
			
			//Strips the lethality number off
			s = s.substring(s.indexOf(',') + 1, s.length());
			int lethality = Integer.parseInt(s);
			
			symptomMap.put(name, new Symptom(name, infectivity, lethality)); // only add to the hashmap if the symptom doesn't already exist
		}
	}
}