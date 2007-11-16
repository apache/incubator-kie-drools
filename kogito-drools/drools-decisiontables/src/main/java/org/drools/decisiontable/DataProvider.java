package org.drools.decisiontable;

public interface DataProvider {

    boolean hasNext();

    String[] next();

}
