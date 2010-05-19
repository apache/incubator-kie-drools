package org.drools.base;

import org.drools.QueryResults;
import org.drools.common.InternalFactHandle;
import org.drools.rule.Declaration;
import org.drools.rule.EntryPoint;
import org.drools.runtime.rule.AttachedViewChangedEventListener;
import org.drools.runtime.rule.impl.OpenQueryViewChangedEventListenerAdapter;

public class AttachedViewChangedEventListenerImpl
    implements
    AttachedViewChangedEventListener {

    public void close() {
        
//        try {
//            startOperation();
//            this.ruleBase.readLock();
//            this.lock.lock();
//            DroolsQuery queryObject = new DroolsQuery( query,
//                                                       arguments,
//                                                       new ExternalViewEventListenerAdapter(listener) );
//            InternalFactHandle handle = this.handleFactory.newFactHandle( queryObject,
//                                                                          this.getObjectTypeConfigurationRegistry().getObjectTypeConf( EntryPoint.DEFAULT,
//                                                                                                                                       queryObject ),
//                                                                          this );
//
//            insert( handle,
//                    queryObject,
//                    null,
//                    null,
//                    this.typeConfReg.getObjectTypeConf( this.entryPoint,
//                                                        queryObject ) );
//
//            this.handleFactory.destroyFactHandle( handle );
//
//        } finally {
//            this.lock.unlock();
//            this.ruleBase.readUnlock();
//            endOperation();
//        }
    }

}
