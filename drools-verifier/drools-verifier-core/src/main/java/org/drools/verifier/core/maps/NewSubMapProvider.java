package org.drools.verifier.core.maps;

import java.util.List;

public interface NewSubMapProvider<Value, MapType extends List<Value>> {

    MapType getNewSubMap();
}
