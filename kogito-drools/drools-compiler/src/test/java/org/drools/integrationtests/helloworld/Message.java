package org.drools.integrationtests.helloworld;

import java.util.ArrayList;
import java.util.List;

public class Message {

	private String message;
	private List list = new ArrayList(  );
    
    
	public Message(String msg) {
		this.message = msg;
	}
	
	public String getMessage() {
		return message;
	}

    public List getList() {
        return list;
    }

    public void setList(List list) {
        this.list = list;
    }
    
    public void addToList(String s) {
        this.list.add( s );
    }

	
}
