package org.drools;

import java.util.ArrayList;
import java.util.List;

public class Cheesery {
    public final static int MAKING_CHEESE = 0;
    public final static int SELLING_CHEESE = 1;
    
    private List cheeses = new ArrayList();
    
    private int status;
    
    public List getCheeses() {
        return this.cheeses;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public int getStatus() {
        return this.status;
    }
}
