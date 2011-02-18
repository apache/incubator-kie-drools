/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.lang.descr;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This is the super type for all pattern AST nodes.
 */
public class AnnotatedBaseDescr extends BaseDescr
    implements
    Externalizable {

    private Map<String, AnnotationDescr> annotations;
    
    private static final long serialVersionUID = 520l;
    
    public AnnotatedBaseDescr() {
        this.annotations = new HashMap<String, AnnotationDescr>();
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal( in );
        this.annotations = (Map<String, AnnotationDescr>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal( out );
        out.writeObject( annotations );
    }
    
    /**
     * Assigns a new annotation to this type
     * @param annotation
     * @return returns the previous value of this annotation
     */
    public AnnotationDescr addAnnotation( AnnotationDescr annotation ) {
        if ( this.annotations == null ) {
            this.annotations = new HashMap<String, AnnotationDescr>();
        }
        return this.annotations.put( annotation.getName(),
                                     annotation );
    }

    /**
     * Assigns a new annotation to this type with the respective name and value
     * @param name
     * @param value
     * @return returns the previous value of this annotation
     */
    public AnnotationDescr addAnnotation( String name,
                                          String value ) {
        if ( this.annotations == null ) {
            this.annotations = new HashMap<String, AnnotationDescr>();
        }
        AnnotationDescr annotation = new AnnotationDescr( name,
                                                          value );
        return this.annotations.put( annotation.getName(),
                                     annotation );
    }

    /**
     * Returns the annotation with the given name
     * @param name
     */
    public AnnotationDescr getAnnotation( String name ) {
        return annotations == null ? null : annotations.get( name );
    }

    /**
    * Returns the set of annotation names for this type
    * @return
    */
    public Set<String> getAnnotationNames() {
        return annotations == null ? null : annotations.keySet();
    }
}
