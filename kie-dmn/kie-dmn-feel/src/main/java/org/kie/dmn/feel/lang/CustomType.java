package org.kie.dmn.feel.lang;

import java.util.Map;

public interface CustomType extends Type {

    Map<String, Property> getProperties();

}
