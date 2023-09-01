package org.drools.core.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ScalablePool<T> {

    private final java.util.Queue<T> pool = new ConcurrentLinkedQueue<>();
    private final List<T> resources = Collections.synchronizedList( new ArrayList<>() );

    private final Supplier<? extends T> supplier;
    private final Consumer<? super T> resetter;
    private final Consumer<? super T> disposer;

    public ScalablePool( int initialSize, Supplier<? extends T> supplier, Consumer<? super T> resetter, Consumer<? super T> disposer ) {
        this.supplier = supplier;
        this.resetter = resetter;
        this.disposer = disposer;

        for (int i = 0; i < initialSize; i++) {
            T t = this.supplier.get();
            pool.offer( t );
            resources.add( t );
        }
    }

    public T get() {
        T t = pool.poll();
        if (t != null) {
            return t;
        }

        t = this.supplier.get();
        resources.add( t );
        return t;
    }

    public void release(T t) {
        resetter.accept( t );
        pool.offer( t );
    }

    public void shutdown() {
        for (T t : resources) {
            disposer.accept( t );
        }
        pool.clear();
        resources.clear();
    }
}
