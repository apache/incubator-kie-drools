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
 * Wrapper for queries. Queries must be written in the appropriate style, no
 * formatting is contributed here.
 * 
 * @author salaboy
 */
public class Queries
    implements
    DRLJavaEmitter {

    private String queriesListing;

    public void setQueriesListing(final String queriesListing) {
        this.queriesListing = queriesListing;
    }

    public void renderDRL(final DRLOutput out) {
        if ( this.queriesListing != null ) {
            out.writeLine( this.queriesListing );
        }
    }

}
