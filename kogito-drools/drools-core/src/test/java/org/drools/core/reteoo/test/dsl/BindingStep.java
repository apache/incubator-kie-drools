/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.reteoo.test.dsl;

import org.drools.core.base.ArrayElements;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.base.extractors.ArrayElementReader;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.core.spi.InternalReadAccessor;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class BindingStep
    implements
    Step {

    private ReteTesterHelper reteTesterHelper;

    public BindingStep(ReteTesterHelper reteTesterHelper) {
        this.reteTesterHelper = reteTesterHelper;
    }

    public void execute(Map<String, Object> context,
                        List<String[]> args) {
        if ( args.size() > 0 ) {
            for( String[] bind : args ) {
                if ( bind.length == 4  ||  bind.length == 5) {
                    createBinding( context,
                                   bind );
                } else {
                    throw new IllegalArgumentException( "Cannot create Binding for arguments: " + Arrays.toString( bind ) );
                }
            }
        } else {
            throw new IllegalArgumentException( "Cannot arguments " + args );
        }
    }

    private void createBinding(Map<String, Object> context,
                               String[] bind ) {
        String name = bind[0];
        String index = bind[1];
        String type = bind[2];
        String field = bind[3];
        String cast = null;
        
        if ( bind.length == 5 ) {
            cast = bind[4];
        }
        

        try {
            Pattern pattern = reteTesterHelper.getPattern( Integer.parseInt( index ),
                                                           type );            
            
            ClassFieldAccessorStore store = (ClassFieldAccessorStore) context.get( "ClassFieldAccessorStore" );
            
            

            InternalReadAccessor extractor = null;
            if ( field.startsWith( "[" ) ) {
                extractor = store.getReader( ArrayElements.class,
                                             "elements" );
                
                extractor = new ArrayElementReader( extractor,
                                                    Integer.parseInt( field.substring( 1, field.length() -1 ) ),
                                                    reteTesterHelper.getTypeResolver().resolveType( cast ) );                
            } else {
                extractor = store.getReader( reteTesterHelper.getTypeResolver().resolveType( type ),
                                             field );
            }            

            Declaration declr = new Declaration( name,
                                                 extractor,
                                                 pattern );
            context.put( name,
                         declr );
        } catch ( Exception e ) {
            throw new IllegalArgumentException( "Cannot create Binding for arguments: " + Arrays.toString( bind ),
                                                e );
        }
    }

}
