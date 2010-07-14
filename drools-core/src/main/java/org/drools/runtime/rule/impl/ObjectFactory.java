package org.drools.runtime.rule.impl;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {
    public FlatQueryResults createFlatQueryResults() {
        return new FlatQueryResults();
    }

}
