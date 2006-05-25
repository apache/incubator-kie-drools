/**
 * 
 */
package org.drools;

public class Message {
    
    private String message1 = "One";
    private String message2 = "Two";
    private String message3 = "Three"; 
    private String message4 = "Four";
    
    
    public static final int HELLO = 0;
    public static final int GOODBYE = 1;
    
    private String message;
    
    private int status;
    
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public int getStatus() {
        return this.status;
    }
    
    public void setStatus( int status ) {
        this.status = status;
    }

    public String getMessage1() {
        return message1;
    }

    public void setMessage1(String message1) {
        this.message1 = message1;
    }

    public String getMessage2() { 
        return message2;
    }

    public void setMessage2(String message2) {
        this.message2 = message2;
    }

    public String getMessage3() {
        return message3; 
    }

    public void setMessage3(String message3) {
        this.message3 = message3;
    }

    public String getMessage4() {
        return message4;
    }

    public void setMessage4(String message4) {
        this.message4 = message4;
    }
}