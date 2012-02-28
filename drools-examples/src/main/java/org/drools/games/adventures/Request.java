package org.drools.games.adventures;

public class Request {
    private long   localId;
    private Object object;
    UserSession    session;

    public Request(UserSession session,
                   Object object) {
        this.object = object;
        this.session = session;
    }

    public UserSession getSession() {
        return session;
    }

    public void setSession(UserSession session) {
        this.session = session;
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
        return "Request [session=" + session + ", localId=" + localId + ", object=" + object + "]";
    }

}
