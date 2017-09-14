package org.drools.model.datasources;

public interface DataStream<T> extends ReactiveDataSource<T> {

    void send( T obj );
}
