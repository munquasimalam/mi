package com.medas.mi;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
//from   ww w .  ja va 2  s  .c o  m
public class Main extends Application {

  @Override
  public void start(Stage primaryStage) {
   
	  // StackPane
	  // Stack pane allows you to place many nodes one on top of an other.
	  StackPane root = new StackPane();
	  //root.setPadding(new Insets(10, 10, 10, 10));
	  Button imgBtn = new Button(" Image ");
	  Label machineName = new Label("Machine name");
	  Button btn1 = new Button(" S ");
	  Button btn2 = new Button("C");
	  Button btn3 = new Button("R");
	 
	  root.getChildren().addAll(imgBtn,machineName,btn1, btn2,btn3);
	  root.setStyle("-fx-background-color: #87CEFA;");
	  
	  // GridPane
	  //GridPane allows you to create a flexible grid of rows and columns and position each node in exact place.
	  GridPane grid = new GridPane();
	  grid.setPadding(new Insets(10, 10, 10, 10));
	  grid.setMinSize(300, 300);
	  grid.setVgap(5);
	  grid.setHgap(5);
	   
	  Text username = new Text("Username:");
	  grid.add(username, 0, 0);
	   
	  TextField text = new TextField();
	  text.setPrefColumnCount(10);
	  grid.add(text, 1, 0);
	   
	  Text password = new Text("Password:");
	  grid.add(password, 0, 1);
	   
	  TextField text2 = new TextField();
	  text2.setPrefColumnCount(10);
	  grid.add(text2, 1, 1);
	  grid.setStyle("-fx-background-color: #D8BFD8;");
   
   
	  // FlowPane
	  //Flow Pane lays all nodes one after another in the order they were added.
	  FlowPane flow = new FlowPane();
	  flow.setPadding(new Insets(10, 10, 10, 10));
	  flow.setStyle("-fx-background-color: DAE6F3;");
	  flow.setHgap(5);
	  flow.getChildren().addAll(new Label("Image"),new Label("Machine Name.....aaaaaaaaaaaaassssssssssssssssssss"),new Button("S"),new Button("C"), new Button("R"));
	  
	 //TilePane
    //TilePane is similar to the flow pane. All nodes are placed in a grid in the same order they were added.
	  
	  TilePane tile = new TilePane();
	  tile.setPadding(new Insets(10, 10, 10, 10));
	  tile.setPrefColumns(2);
	  tile.setStyle("-fx-background-color: #CD5C5C;");
	  HBox hbox2 = new HBox(8); // spacing = 8
	  hbox2.getChildren().addAll(new Button("Top"), new Button("Left"), new Button("centre"));
	  tile.getChildren().add(hbox2);
	  
	 // AnchorPane
	  //AnchorPane allows you to position nodes in the top, bottom, left side, right side, or center of the pane.
	  AnchorPane anchorpane = new AnchorPane();
	  TextArea textArea = new TextArea();
	  textArea.appendText("mmmmmmmmmmm \n");
	  textArea.appendText("mmmmmmmmmmm \n");
	  textArea.appendText("mmmmmmmmmmm \n");
	  textArea.appendText("mmmmmmmmmmm \n");
	  textArea.appendText("mmmmmmmmmmm \n");
	  textArea.appendText("mmmmmmmmmmm \n");
	  textArea.appendText("mmmmmmmmmmm \n");
	  textArea.appendText("mmmmmmmmmmm \n");
	  textArea.appendText("mmmmmmmmmmm \n");
	  textArea.appendText("mmmmmmmmmmm \n");
	  textArea.appendText("mmmmmmmmmmm \n");
	  textArea.appendText("mmmmmmmmmmm \n");
	  textArea.appendText("mmmmmmmmmmm \n");
	  textArea.appendText("mmmmmmmmmmm \n");
	  textArea.appendText("mmmmmmmmmmm \n");
	  textArea.appendText("mmmmmmmmmmm \n");
	  textArea.appendText("mmmmmmmmmmm \n");
	  textArea.appendText("mmmmmmmmmmm \n");
	 // Button buttonSave = new Button("Save");
	 // Button buttonCancel = new Button("Cancel");
	  anchorpane.setStyle("-fx-background-color: #A9A9A9;");
	 // HBox hb = new HBox();
	  anchorpane.getChildren().addAll(textArea);
	 // anchorpane.getChildren().addAll(hb);
	  anchorpane.setMinSize(200, 200);
	
	 // AnchorPane.setRightAnchor(hb, 10.0);
	  
	  //BorderPane
      //BorderPane splits the scene in five regions such as: top, bottom, left, right, and center. Where you can adjust added nodes. BorderPane also allows you to add different panes in each region as shown in my example. However  you cannot use the same pane more than once.
	  BorderPane pane = new BorderPane();
//	  pane.setLeft(anchorpane);
	  //pane.setCenter(root);
	  pane.setCenter(anchorpane);
	 
//	  pane.setRight(grid);
	  pane.setTop(flow);
	  //pane.setTop(root);
	  //pane.setBottom(tile);
	   
	  Scene scene = new Scene(pane, 480, 200);

    primaryStage.setScene(scene);
    primaryStage.show();
  }
  public static void main(String[] args) {
    launch(args);
  }
}
