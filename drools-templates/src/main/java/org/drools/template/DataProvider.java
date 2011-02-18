package org.drools.template;

public interface DataProvider {

    boolean hasNext();

    String[] next();

}
