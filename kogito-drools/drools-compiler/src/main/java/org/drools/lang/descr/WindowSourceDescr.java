/*
 * Copyright 2007 JBoss Inc
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
 * Created on Dec 13, 2007
 */
package org.drools.lang.descr;

/**
 * A pattern source descriptor for windows
 */
public class WindowSourceDescr extends PatternSourceDescr {

    private static final long serialVersionUID = 150l;
    
    public WindowSourceDescr() {
    }
    
    public WindowSourceDescr( String name ) {
        this.setText( name );
    }
    
    public void setName( String name ) {
        this.setText( name );
    }
    
    public String getName() {
        return this.getText();
    }
    
    @Override
    public String toString() {
        return "from window \""+getName()+"\"";
    }

}
