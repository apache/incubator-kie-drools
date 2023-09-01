package org.drools.drl.parser.lang;

public interface ExpanderResolver {

    Expander get(String name,
                 String config);

}
