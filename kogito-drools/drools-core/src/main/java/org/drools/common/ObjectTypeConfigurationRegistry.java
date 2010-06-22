package org.drools.common;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.drools.facttemplates.Fact;
import org.drools.reteoo.ClassObjectTypeConf;
import org.drools.reteoo.FactTemplateTypeConf;
import org.drools.reteoo.ObjectTypeConf;
import org.drools.rule.EntryPoint;

public class ObjectTypeConfigurationRegistry implements Serializable {
    private static final long serialVersionUID = -7049575828126061047L;
    
    private InternalRuleBase ruleBase;
    private ConcurrentMap<Object, ObjectTypeConf> typeConfMap;
    

    
    public ObjectTypeConfigurationRegistry(InternalRuleBase ruleBase ) {
        super();
        this.ruleBase = ruleBase;
        this.typeConfMap = new ConcurrentHashMap<Object, ObjectTypeConf>();
    }



    /**
     * Returns the ObjectTypeConfiguration object for the given object or
     * creates a new one if none is found in the cache
     * 
     * @param object
     * @return
     */
    public ObjectTypeConf getObjectTypeConf(EntryPoint entrypoint,
                                            Object object) {
        
        // first see if it's a ClassObjectTypeConf        
        ObjectTypeConf objectTypeConf = null;
        Class<?> cls = null;
        if ( object instanceof Fact ) {
            String key = ((Fact) object).getFactTemplate().getName();
            objectTypeConf = (ObjectTypeConf) this.typeConfMap.get( key );            
        } else {
            cls = object.getClass();
            objectTypeConf = this.typeConfMap.get( cls );            
        }                       
        
        // it doesn't exist, so create it.
        if ( objectTypeConf == null ) {
            if ( object instanceof Fact ) {;
                objectTypeConf = new FactTemplateTypeConf( entrypoint,
                                                           ((Fact) object).getFactTemplate(),
                                                           this.ruleBase );           
                this.typeConfMap.put( ((Fact) object).getFactTemplate().getName(), 
                                      objectTypeConf );
            } else {
                objectTypeConf = new ClassObjectTypeConf( entrypoint,
                                                          cls,
                                                          this.ruleBase );
                this.typeConfMap.put( cls, objectTypeConf );
            }            
        }

        return objectTypeConf;
    }

    
    public Collection<ObjectTypeConf> values() {
        return this.typeConfMap.values();
    }
}
