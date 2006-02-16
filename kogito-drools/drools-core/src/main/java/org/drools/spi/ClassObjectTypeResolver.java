package org.drools.spi;

import org.drools.base.ClassObjectType;

/**
 * 
 * @author mproctor
 *
 */
public class ClassObjectTypeResolver implements ObjectTypeResolver {
    ModifieableClassObjectType objectType = new ModifieableClassObjectType( null );

    public ObjectType resolve(Object object) {
        this.objectType.setClass( object.getClass() );
        return this.objectType;
    }
    
    
    static class ModifieableClassObjectType extends ClassObjectType {

        public ModifieableClassObjectType(Class objectTypeClass) {
            super( objectTypeClass );
        }
        
        public void setClass(Class clazz) {
            this.objectTypeClass = clazz;
        }
        
    }
}
