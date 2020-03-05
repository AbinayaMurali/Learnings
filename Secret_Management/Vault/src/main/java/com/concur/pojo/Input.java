package com.concur.pojo;

import java.util.HashMap;

public class Input {
	private HashMap<String,String> data = new HashMap<String,String>();

	public HashMap<String,String> getData() {
		return data;
	}

	public void setData(String key, String value) {
		this.data.put(key, value);
	}
}
