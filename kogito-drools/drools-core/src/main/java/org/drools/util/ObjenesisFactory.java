/*
 * Copyright 2008 JBoss Inc
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
 *
 * Created on Mar 17, 2008
 */
package org.drools.util;

import java.io.Serializable;

import org.drools.objenesis.Objenesis;
import org.drools.objenesis.ObjenesisStd;

/**
 * A factory interface for Objenesis instances
 * 
 * @author etirelli
 */
public class ObjenesisFactory implements Serializable {
    
    private static final long serialVersionUID = 969174504278340793L;
    private static Objenesis OBJENESIS_INSTANCE = null;

    private ObjenesisFactory() {}
    
    /**
     * Returns a statically cached objenesis instance 
     */
    public static Objenesis getStaticObjenesis() {
        if( OBJENESIS_INSTANCE == null ) {
            OBJENESIS_INSTANCE = new ObjenesisStd(true);
        }
        return OBJENESIS_INSTANCE;
    }

    /**
     * Returns a newly instantiated objenesis instance
     */
    public static Objenesis getDefaultObjenesis() {
        return new ObjenesisStd(true);
    }

}
