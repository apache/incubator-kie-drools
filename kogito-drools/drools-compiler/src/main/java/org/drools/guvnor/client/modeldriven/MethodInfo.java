package org.drools.guvnor.client.modeldriven;

import java.util.Iterator;
import java.util.List;

import org.drools.guvnor.client.modeldriven.brl.PortableObject;

/**
 * 
 * @author Toni Rikkola
 *
 */
public class MethodInfo
    implements
    PortableObject {

    private String       name;
    private List<String> params;

    public MethodInfo() {

    }

    public MethodInfo(String name,
                      List<String> params) {
        this.name = name;
        this.params = params;
    }

    public String getNameWithParameters() {

        String n = name + "(";

        for ( Iterator<String> iterator = params.iterator(); iterator.hasNext(); ) {
            n += iterator.next();

            if ( iterator.hasNext() ) {
                n += ",";
            }
        }

        n += ")";

        return n;
    }

    public String getName() {
        return name;
    }

    public List<String> getParams() {
        return params;
    }

    public boolean equals(Object o) {
        if ( o instanceof MethodInfo ) {
            MethodInfo m = (MethodInfo) o;

            if ( this.getName().equals( m.getName() ) && this.getParams().size() == m.getParams().size() ) {
                int i = 0;
                for ( String param : this.params ) {
                    param.equals( m.getParams().get( i ) );
                    i++;
                }
            }
        }

        return false;
    }

    public int hashCode() {
        int hash = name.hashCode();

        int i = 0;
        for ( String p : params ) {
            hash = hash + (p.hashCode() * i);
            i++;
        }

        return hash;
    }
}
