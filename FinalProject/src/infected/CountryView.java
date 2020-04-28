package infected;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import javafx.scene.control.TableRow;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

/*
 * CountryView method called from main to the next stage from the button action made from
 *the user. This method is responsible for creating a table of country object values from each and a
 *button for it to update with new values utilizing every other class made
 *
 */
public class CountryView {
		private ArrayList<Country> countries;
		private World world;
		private final BorderPane rootPane;
	    TableView<Country> table;
	    private TableColumn<Country, String> colName;
	    private TableColumn<Country, Integer> colHealth, colInfected, colDeceased;
	    private Label dayLabel;
	    private long goal; // this determines which goal you're at to upgrade the symptoms
	    HashMap<String, Symptom> symptomMap; //symptommap will make sure that no duplicate symptoms are chosen
	    
	    /*
	     * ---Button Action---
	     * Button from main leads to this method where it loads the next scene
	     */
	    public CountryView(World world, HashMap<String, Symptom> symptomMap) {
	    	
	    	//Grab the variables that come with World object
	    	this.world = world;
	    	this.symptomMap = symptomMap;
	    	this.goal = 5000000;
	    	this.countries = world.getCountries();
	    	
	    	//Grab the virus that the user inputted from world object
	    	symptomMap.put(world.getVirus().getName(), world.getVirus().getSymptoms().get(0));
	    	
	        //Name the column for all the country names
	        colName = new TableColumn<>("Country Name");
	        colName.setMinWidth(250);
	        
	        // create a new cellvaluefactory that gets the name property from the country object
	        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
	        
	        //Name the column for all the # populations within each country
	        colHealth = new TableColumn<>("Healthy");
	        colHealth.setMinWidth(200);
	        
	         // create a new cellvaluefactory that gets the healthy property from the country object
	        colHealth.setCellValueFactory(new PropertyValueFactory<>("healthy"));
	        
	        //Name a column for all the # of infected within each country
	        colInfected = new TableColumn<>("Infected");
	        colInfected.setMinWidth(200);
	        
	        // create a new cellvaluefactory that gets the infected property from the country object
	        colInfected.setCellValueFactory(new PropertyValueFactory<>("infected"));
	        
	        //Name a column for all the # of those dead within each country
	        colDeceased = new TableColumn<>("Dead");
	        colDeceased.setMinWidth(200);
	        
	        // create a new cellvaluefactory that gets the deceased property from the country object
	        colDeceased.setCellValueFactory(new PropertyValueFactory<>("deceased"));
	        
	    	// create new table with values from the country objects
	        table = new TableView<Country>();
	        // Create a row factory to determine if the priority is high or low 	        
	        
	        
	        // add all columns to the table
	        table.getColumns().addAll(colName, colHealth, colInfected, colDeceased);
	        
	        /*
	         * Everytime that the table is updated, this method is called for it to
	         * determine the sick population to set its color
	         */
	        table.setRowFactory(row -> new TableRow<Country>() {
	            @Override
	            public void updateItem(Country item, boolean empty) {
	                super.updateItem(item, empty);
	                if (item != null)
	                {
	                	if (item.getInfected() > 1)
	                	{
		                	// convert percentage to a number between 0-255 since styling uses hex
		                	int severity = (item.getInfected()  / item.getPop() * 255);  
		                	// left shift 16 bits in order to make the rgb hex representation
		                	severity = severity << 16;
		                	setStyle("-fx-background-color: #" + String.format("%04X", severity) + ";");
	                	}
	                }
	                else
	                {
	                	// default style
	                	setStyle("");
	                }
	            }
	        });
	        
	        // add all the countries 
	        for (Country c: countries)
	        {
	        	table.getItems().add(c);
	        }
	        //Create a HBox for the table and set its position in the center
	        HBox tableBox = new HBox(table);
	        tableBox.setAlignment(Pos.CENTER);
	                        
	        //Create a label for the change of the day
	        //Set the size of the label
	        dayLabel = new Label();
	        dayLabel.setPrefSize(70,70);
	        
	        //Method to display the change of the day on the frontend
	        updateDayLabel();
	        
	        // Paste the table on the stage
	        rootPane = new BorderPane(tableBox);
	                        
	        //Create a button to update the info of each country by the # of days
	        Button nextDayButton = new Button("Next Day");
	        nextDayButton.setPrefSize(70, 70);
	        
	        //---Button Action--- -> Goes to update() method to increment the day
	        //////////////////////////////////////////////
	        nextDayButton.setOnAction(e -> update());
	        //////////////////////////////////////////////
	        
	        //Horizontal box to show the number of days and the button
	        HBox hbox = new HBox(dayLabel, nextDayButton);
	        hbox.setAlignment(Pos.CENTER);
	                        
	        //Place the HBox location at the bottom
	        rootPane.setBottom(hbox);
	        rootPane.setPrefSize(70, 70);
	    }
	    /*
	     * ---Button Action--- 
	     * Called from nextDayButton to increment the day
	     */
	    private void updateDayLabel() {
	    	dayLabel.setText("Day: " + world.getDay());
	    }
	    
	    public void update(){
	    	world.incrementDay();
	    	updateDayLabel();
	    	// update the tableview
	    	table.refresh();
	    	if (world.getTotalSick() > goal)
	    	{
	    		Set set = symptomMap.entrySet();
	    		Iterator iterator = set.iterator();
	    		ArrayList<String> choices = new ArrayList<String>();
	    		// iterate through the hashmap in order to populate the choice dialogue combo box
	    		while (iterator.hasNext())
	    		{
	    			Map.Entry<String, Symptom> mentry = (Map.Entry<String, Symptom>) iterator.next();
	    			choices.add(mentry.getKey());
	    		}
	    		ChoiceDialog<String> dialog = new ChoiceDialog<>(choices.get(1), choices);
	    		dialog.setTitle("Oh no!");
	    		dialog.setHeaderText(world.getVirus().getName() + " has infected more than " + goal + " people...\nYou can upgrade now.");
	    		dialog.setContentText("Choose a new symptom:");

	    		// Traditional way to get the response value.
	    		Optional<String> result = dialog.showAndWait();
	    		// if the user chose yes
	    		if (result.isPresent()){
	    			// get the result and use it as a key to get the virus object from the hashmap
	    		    world.getVirus().addSymptom(symptomMap.get(result.get()));
	    		    // remove the object selected from the hashmap
	    		    symptomMap.remove(result.get());
	    		}
	    		goal *= 10; // increase the goal
	    	}
	    } 


	    public Pane getRootPane() {
	        return rootPane;
	    }
}