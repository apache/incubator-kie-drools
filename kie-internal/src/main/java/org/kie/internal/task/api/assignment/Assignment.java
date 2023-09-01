package org.kie.internal.task.api.assignment;

import java.io.Serializable;

public class Assignment implements Serializable, Comparable<Assignment>{

    private static final long serialVersionUID = -6569072011166557126L;

    private String user;

    public Assignment(String user) {
        super();
        this.user = user;
    }

    public String getUser(){
        return this.user;
    }

    @Override
    public String toString() {
        return "Assignment [user=" + user + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((user == null) ? 0 : user.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Assignment other = (Assignment) obj;
        if (user == null) {
            if (other.user != null) {
                return false;
            }
        } else if (!user.equals(other.user)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Assignment o) {
        if (this.user == null) {
            return -1;
        }
        return this.user.compareTo(o.getUser());
    }
}
