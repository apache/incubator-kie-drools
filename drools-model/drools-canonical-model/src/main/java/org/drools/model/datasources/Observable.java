package org.drools.model.datasources;

public interface Observable {
    void addObserver( DataSourceObserver o );
    void deleteObserver( DataSourceObserver o );
}
