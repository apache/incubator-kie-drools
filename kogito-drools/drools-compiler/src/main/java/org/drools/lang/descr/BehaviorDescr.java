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

package org.drools.lang.descr;

/**
 * A super class for all Behavior Descriptors like
 * time window, event window, distinct, etc
 *  
 *
 */
public class BehaviorDescr extends BaseDescr {
    
    /**
     * @param type
     */
    public BehaviorDescr() {
        super();
    }
    
    /**
     * @param type
     */
    public BehaviorDescr(String type) {
        super();
        setText(type);
    }

    /**
     * @return the type
     */
    public String getType() {
        return getText();
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        setText( type );
    }
    
    

}
