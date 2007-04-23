package org.drools;

import java.util.Iterator;
import java.util.List;

import org.drools.event.AgendaEventListener;
import org.drools.spi.AgendaFilter;
import org.drools.spi.GlobalResolver;
import org.drools.util.concurrent.locks.BlockingQueue;
import org.drools.util.concurrent.locks.LinkedBlockingQueue;
import org.drools.util.concurrent.locks.Queue;

public class ReteConcurrentStatelessSession implements ConcurrentStatelessSession {    
    private BlockingQueue queue;
    
    public void asyncExecute(Object object) {
        this.queue.offer( object );
    }
    
    public void asyncExecute(Object[] list) {
        this.queue.offer( list );
    }
    
    public void asyncExecute(List list) {
        this.queue.offer( list );
    }
    
 
    public class ProducerConsumer implements Runnable {
        private WorkingMemory workingMemory;
        private BlockingQueue queue;
        
        
        public ProducerConsumer(WorkingMemory workingMemory, BlockingQueue queue) {
            this.workingMemory = workingMemory;
            this.queue = queue;
        }

        public void run() {
            while (true) {
                try {
                    Object object = this.queue.take();
                    if ( object instanceof Object[] ) {
                        this.workingMemory.assertObject( object );
                    } else if ( object instanceof List ) {
                        List list = ( List ) object;
                        for ( Iterator it = list.iterator(); it.hasNext(); ) {
                            this.workingMemory.assertObject( it.next() );
                        }
                    } else {
                        Object[] objects = ( Object[] ) object;
                        for ( int i = 0, length = objects.length; i < length; i++ ) {
                            this.workingMemory.assertObject( objects[i] );
                        }
                    }
                    Thread.sleep( 100 );
                } catch(InterruptedException e) {
                    return;
                }
            }
        }        
    }
}
