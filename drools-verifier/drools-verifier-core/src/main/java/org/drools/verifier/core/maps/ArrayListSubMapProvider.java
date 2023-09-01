package org.drools.verifier.core.maps;

import java.util.ArrayList;

//import org.jboss.errai.common.client.api.annotations.Portable;

//@Portable
public class ArrayListSubMapProvider<Value>
        implements NewSubMapProvider<Value, ArrayList<Value>> {

    public ArrayListSubMapProvider() {
    }

    @Override
    public ArrayList<Value> getNewSubMap() {
        return new ArrayList<>();
    }
}
