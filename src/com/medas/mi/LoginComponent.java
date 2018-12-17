
package com.medas.mi;

import com.medas.mi.utils.AlertUtil;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Reflection;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * This class is used for login Component.
 * @author Alam
 */
public class LoginComponent {
  static TextField userText = null;
  static Stage loginStage = null;
  static MenuItem menuItemLogin = null;
  static Text errorText = null;

  String user = "mm";
  String pw = "mm";
  String checkUser, checkPw;
  
 // SerialPortControls serialPortCtrl = new SerialPortControls();
  
  /**
   * This method is used to open the Login window. 
 * @param menuItemLogin parameter value will be  login initially 
 * after successful login  it will be logout.  
 */
  public  void showLoginScreen(final MenuItem menuItemLogin) {
	  
	  
	  loginStage = new Stage();
      BorderPane bp = new BorderPane();
      bp.setPadding(new Insets(10,50,50,50));
     
      //Adding HBox
      HBox hb = new HBox();
      hb.setPadding(new Insets(20,20,20,30));
     
      //Adding GridPane
      GridPane gridPane = new GridPane();
      gridPane.setPadding(new Insets(20,20,20,20));
      gridPane.setHgap(5);
      gridPane.setVgap(5);
     
     //Implementing Nodes for GridPane
      Label lblUserName = new Label("User Name");
      final TextField txtUserName = new TextField();
      Label lblPassword = new Label("Password");
      final PasswordField pf = new PasswordField();
      Button btnLogin = new Button("Login");
      final Label lblMessage = new Label();
     
      //Adding Nodes to GridPane layout
      gridPane.add(lblUserName, 0, 0);
      gridPane.add(txtUserName, 1, 0);
      gridPane.add(lblPassword, 0, 1);
      gridPane.add(pf, 1, 1);
      gridPane.add(btnLogin, 2, 1);
      gridPane.add(lblMessage, 1, 2);
     
             
      //Reflection for gridPane
      Reflection r = new Reflection();
      r.setFraction(0.7f);
      gridPane.setEffect(r);
     
      //DropShadow effect
      DropShadow dropShadow = new DropShadow();
      dropShadow.setOffsetX(5);
      dropShadow.setOffsetY(5);
     
      //Adding text and DropShadow effect to it
      Text text = new Text("Login Form");
      text.setFont(Font.font("Courier New", FontWeight.BOLD, 28));
      text.setEffect(dropShadow);
     
      //Adding text to HBox
      hb.getChildren().add(text);
                       
      //Add ID's to Nodes
      bp.setId("bp");
      gridPane.setId("root");
      btnLogin.setId("btnLogin");
      text.setId("text");
             
      //Action for btnLogin
      btnLogin.setOnAction(new EventHandler<ActionEvent>() {
    	  @Override
            public void handle(ActionEvent event) {
                      checkUser = txtUserName.getText().toString();
                      checkPw = pf.getText().toString();
                      if(checkUser.equals(user) && checkPw.equals(pw)){
                              lblMessage.setText("Congratulations!");
                              lblMessage.setTextFill(Color.GREEN);
                              AlertUtil.loginSuccessAlert(txtUserName.getText());
                            loginStage.close(); // return to main window
                           menuItemLogin.setText("Logout");
                          // serialPortCtrl.setVisibility();
                      }
                      else{
                              lblMessage.setText("Incorrect user or pw.");
                              lblMessage.setTextFill(Color.RED);
                      }
                      txtUserName.setText("");
                      pf.setText("");
              }
              });
     
      //Add HBox and GridPane layout to BorderPane Layout
      bp.setTop(hb);
      bp.setCenter(gridPane);  
     
      //Adding BorderPane to the scene and loading CSS
      if ("Logout".equalsIgnoreCase(menuItemLogin.getText())) {
      Scene scene = new Scene(bp);
      scene.getStylesheets().add(getClass().getClassLoader().getResource("com/medas/mi/css/login.css").toExternalForm());
      loginStage.setScene(scene);
      loginStage.titleProperty().bind(
                  scene.widthProperty().asString().
                  concat(" : ").
                  concat(scene.heightProperty().asString()));
         loginStage.show();
      }
  }
	  
	  
	  
	  
	  
//    loginStage = new Stage();
//    VBox vBox = new VBox();
//    vBox.setPadding(new Insets(25, 25, 25, 25));//
//    vBox.setAlignment(Pos.CENTER);
//    Label userLabel = new Label("User Name:");
//    userText = new TextField();
//    Label passLabel = new Label("Password:");
//    PasswordField passField = new PasswordField();
//    Button btnLogin = new Button("Login");
//    errorText = new Text();
//    btnLogin.setOnAction(new EventHandler<ActionEvent>() {
//      @Override
//    public void handle(ActionEvent event) {
//     if("mm".equals(userText.getText()) &&
//     "mm".equals(passField.getText())){
//       // if ("mm".equals("mm") && "mm".equals("mm")) {
//          AlertUtil.loginSuccessAlert(userText.getText());
//          loginStage.close(); // return to main window
//          menuItemLogin.setText("Logout");
//        } else {
//          errorText.setText("Incorrect user Name OR  Password");
//          errorText.setFill(Color.FIREBRICK);
//        }
//      }
//    });
//    if ("Logout".equalsIgnoreCase(menuItemLogin.getText())) {
//      vBox.getChildren().add(0, userLabel);
//      vBox.getChildren().add(1, userText);
//      vBox.getChildren().add(2, passLabel);
//      vBox.getChildren().add(3, passField);
//      vBox.getChildren().add(4, btnLogin);
//      vBox.getChildren().add(5, errorText);
//      Scene scene = new Scene(vBox, 250, 150);
//      loginStage.setScene(scene);
//      loginStage.initStyle(StageStyle.UTILITY);
//      loginStage.show();
//    }
//  }
}
