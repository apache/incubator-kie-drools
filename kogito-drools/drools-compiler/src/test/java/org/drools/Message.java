package org.drools;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Message implements Serializable {

    private String          message1 = "One";
    private String          message2 = "Two";
    private String          message3 = "Three";
    private String          message4 = "Four";

    public static final int HELLO    = 0;
    public static final int GOODBYE  = 1;

    private String          message;

    private int             status;

    private List    list     = new ArrayList();
    private int     number   = 0;
    private Date    birthday = new Date();
    private boolean fired    = false;

    public Message() {
    }

    public Message(final String msg) {
        this.message = msg;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(final int status) {
        this.status = status;
    }

    public String getMessage1() {
        return this.message1;
    }

    public void setMessage1(final String message1) {
        this.message1 = message1;
    }

    public String getMessage2() {
        return this.message2;
    }

    public void setMessage2(final String message2) {
        this.message2 = message2;
    }

    public String getMessage3() {
        return this.message3;
    }

    public void setMessage3(final String message3) {
        this.message3 = message3;
    }

    public String getMessage4() {
        return this.message4;
    }

    public void setMessage4(final String message4) {
        this.message4 = message4;
    }

    public boolean isFired() {
        return this.fired;
    }

    public void setFired(final boolean fired) {
        this.fired = fired;
    }

    public Date getBirthday() {
        return this.birthday;
    }

    public void setBirthday(final Date birthday) {
        this.birthday = birthday;
    }

    public int getNumber() {
        return this.number;
    }

    public void setNumber(final int number) {
        this.number = number;
    }

    public List getList() {
        return this.list;
    }

    public void setList(final List list) {
        this.list = list;
    }

    public void addToList(final String s) {
        this.list.add( s );
    }

    public String toString() {
        return "Message{" +
                "status=" + status +
                ", message='" + message + '\'' +
                '}';
    }
}
