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

package org.drools.template.model;

/**
 * Wrapper for functions. Functions must be written in the appropriate style, no 
 * formatting is contributed here.
 * 
 * @author Michael Neale
 */
public class Functions
    implements
    DRLJavaEmitter {

    private String functionsListing;

    public void setFunctionsListing(final String functionsListing) {
        this.functionsListing = functionsListing;
    }

    public void renderDRL(final DRLOutput out) {
        if ( this.functionsListing != null ) {
            out.writeLine( this.functionsListing );
        }
    }

}
