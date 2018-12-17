package com.medas.mi.utils;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DropdownUtil {
	private String key; ;
	private String value;
	
	public DropdownUtil(String key, String value) {
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	public static ObservableList<String> getProperties(){
		ObservableList<String> data	= FXCollections.observableArrayList();
		data.addAll(new String("key"));
		data.addAll(new String("key2"));
		return data;
	}

}
