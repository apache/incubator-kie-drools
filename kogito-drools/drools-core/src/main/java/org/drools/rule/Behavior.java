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
 * Created on Apr 25, 2008
 */

package org.drools.rule;

import org.drools.spi.RuleComponent;

/**
 * An interface for all behavior implementations
 * 
 * @author etirelli
 *
 */
public interface Behavior extends RuleComponent, Cloneable {
    
    public static final Behavior[] EMPTY_BEHAVIOR_LIST = new Behavior[0];
    
    public enum BehaviorType {
        TIME_WINDOW( "time" ),
        LENGTH_WINDOW( "length" );
        
        private final String id;
        
        private BehaviorType( String id ) {
            this.id = id;
        }
        
        public boolean matches( String id ) {
            return this.id.equalsIgnoreCase( id );
        }
    }
    
    public BehaviorType getType();

}
