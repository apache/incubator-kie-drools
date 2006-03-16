package org.drools.integrationtests.helloworld;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Message {

	private String message;
	private List list = new ArrayList(  );
    private int number = 0;
    private Date birthday = new Date();
    
    
	public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

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
