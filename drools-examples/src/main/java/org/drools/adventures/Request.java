package org.drools.adventures;

public class Request {
    private long remoteId;
    private long localId;
    private Object object;
    
    public Request(Object object) {
        this.object = object;
    }
    
    public long getRemoteId() {
        return remoteId;
    }
    
    public void setRemoteId(long id) {
        this.remoteId = id;
    }
    
    public Object getObject() {
        return object;
    }

    public long getLocalId() {
        return localId;
    }

    public void setLocalId(long localId) {
        this.localId = localId;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    @Override
    public String toString() {
        return "Request [remoteId=" + remoteId + ", localId=" + localId + ", object=" + object + "]";
    }
    
}
