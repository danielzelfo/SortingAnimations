/*  Program: Sorting Algorithms Animations
 *	Author: Daniel Zelfo
 *	Class: CSCI 230
 *	Date: 4/20/2020
 *	Description: This is a JavaFX that can be used to visualize sorting animations
 *				 It allows the user to generate data that is random, ascending, or descending with their choice of the size
 *				 The user can then choose which algorithm to use to sort the data
 *				 The algorithms include: Insertion Sort, Selection Sort, Shell Sort, Heap Sort, and three variations of QuickSort
 *
 *
 I certify that the code below is my own work.
 *
 *	Exception(s): N/A
 *
 */

package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.fxml.FXMLLoader;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			
			VBox root = (VBox)FXMLLoader.load(getClass().getResource("SortAnim.fxml"));
			
			Scene scene = new Scene(root,625,500);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
