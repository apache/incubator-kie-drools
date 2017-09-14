package org.drools.model.datasources;

public interface DataSourceObserver<T> {

    boolean objectInserted( T obj );

    boolean objectUpdated( T obj );

    boolean objectDeleted( T obj );
}
