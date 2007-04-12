/*
 * Copyright 2006 JBoss Inc
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

package org.drools.base;

import java.lang.reflect.Field;

import org.drools.RuntimeDroolsException;

/**
 * A helper class with utility methods
 * 
 * @author etirelli
 */
public class ShadowProxyHelper {

    public static void copyState(final ShadowProxy from,
                                 final ShadowProxy to) {
        final Field[] fields = from.getClass().getDeclaredFields();
        for ( int i = 0; i < fields.length; i++ ) {
            if ( fields[i].getName().endsWith( ShadowProxyFactory.FIELD_SET_FLAG ) ) {
                fields[i].setAccessible( true );
                try {
                    if ( fields[i].getBoolean( from ) ) {
                        final String fieldName = fields[i].getName().substring( 0,
                                                                          fields[i].getName().length() - ShadowProxyFactory.FIELD_SET_FLAG.length() );
                        final Field flag = to.getClass().getDeclaredField( fields[i].getName() );
                        final Field fieldFrom = from.getClass().getDeclaredField( fieldName );
                        final Field fieldTo = to.getClass().getDeclaredField( fieldName );
                        flag.setAccessible( true );
                        fieldFrom.setAccessible( true );
                        fieldTo.setAccessible( true );

                        // we know it is set
                        flag.setBoolean( to,
                                         true );
                        // copy the value from "from" shadow proxy
                        fieldTo.set( to,
                                     fieldFrom.get( from ) );
                    }
                } catch ( final Exception e ) {
                    throw new RuntimeDroolsException( "Unable to copy state from one shadow proxy to another" );
                }

            }
        }
    }

}
