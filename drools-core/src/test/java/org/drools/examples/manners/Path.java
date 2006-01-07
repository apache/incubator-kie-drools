package org.drools.examples.manners;

public class Path {
    private final int id;
    private final String guestName;
    private final int seat;
    
    public Path(int id, int seat, String guestName) {
        this.id = id;
        this.seat = seat;
        this.guestName = guestName;
    }

    public int getSeat() {
        return this.seat;
    }    
    
    public String getGuestName() {
        return this.guestName;
    }

    public int getId() {
        return this.id;
    }
    
    
    
}
