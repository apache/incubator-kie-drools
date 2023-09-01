package org.drools.base.factmodel.traits;

import java.util.Map;

@Trait
public interface Thing<K> {

    Map<String,Object> getFields();

    K getCore();

    boolean isTop();

}
