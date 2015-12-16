/*
 * Copyright 2008 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.lang.descr;

import java.util.List;

/**
 * A super class for all Behavior Descriptors like
 * time window, event window, distinct, etc
 */
public class BehaviorDescr extends BaseDescr {
    
    private String subtype;
    private List<String> params;
    
    /**
     * @param type
     */
    public BehaviorDescr() { }
    
    /**
     * @param type
     */
    public BehaviorDescr(String type) {
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

    public void setSubType( String subtype ) {
        this.subtype = subtype;
    }
    
    public void setParameters( List<String> params ) {
        this.params = params;
    }

    public String getSubType() {
        return subtype;
    }

    public List<String> getParameters() {
        return params;
    }

}
