/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.medas.mi.utils;

import javafx.scene.control.Alert;

/**
 *
 * @author MEDAS87
 */
public class AlertUtil {
    
   public static void loginSuccessAlert(String user) {
       
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Successful login");
        alert.setHeaderText("Successful login");
        String s = user + " logged in!";
        alert.setContentText(s);
        alert.show();
    } 
   
   public static void connectionSuccessAlert(String Port) {
       
       Alert alert = new Alert(Alert.AlertType.INFORMATION);
       alert.setTitle("Connected Successfully");
       alert.setHeaderText("Successful Connection");
       String s = "Connected with Port:" +Port;
       alert.setContentText(s);
       alert.show();
   } 
   
 public static void portInUseAlert(String Port) {
       
       Alert alert = new Alert(Alert.AlertType.INFORMATION);
       alert.setTitle("PortInUseException");
       alert.setHeaderText("This Port is already in used Use Other Potr");
       String s = " Not Connected with Port:" +Port;
       alert.setContentText(s);
       alert.show();
   } 
   
   
   
}
