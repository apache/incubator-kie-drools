package org.drools.kiesession.audit;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * An object event logged by the WorkingMemoryLogger.
 * It is a snapshot of the event as it was thrown by the working memory.
 * It contains the fact id and a String represention of the object
 * at the time the event was logged.
 */
public class ObjectLogEvent extends LogEvent {

    private long   factId;
    private String objectToString;

    /**
     * Create a new activation log event.
     * 
     * @param type The type of event.  This can only be LogEvent.OBJECT_ASSERTED,
     * LogEvent.OBJECT_MODIFIED or LogEvent.OBJECT_RETRACTED.
     * @param factId The id of the fact
     * @param objectToString A toString of the fact 
     */
    public ObjectLogEvent(final int type,
                          final long factId,
                          final String objectToString) {
        super( type );
        this.factId = factId;
        this.objectToString = objectToString;
    }

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        factId    = in.readLong();
        objectToString    = (String)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeLong(factId);
        out.writeObject(objectToString);
    }

    /**
     * Returns the fact id of the object this event is about.
     * 
     * @return the id of the fact
     */
    public long getFactId() {
        return this.factId;
    }

    /**
     * Returns a toString of the fact this event is about at the
     * time the event was created.
     * 
     * @return the toString of the fact
     */
    public String getObjectToString() {
        return this.objectToString;
    }

    public String toString() {
        String msg = null;
        switch ( this.getType() ) {
            case INSERTED :
                msg = "OBJECT ASSERTED";
                break;
            case UPDATED :
                msg = "OBJECT MODIFIED";
                break;

            case RETRACTED :
                msg = "OBJECT RETRACTED";
                break;
        }
        return msg + " value:" + this.objectToString + " factId: " + this.factId;

    }
}
