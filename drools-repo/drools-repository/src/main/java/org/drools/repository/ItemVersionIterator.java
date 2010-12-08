/**
 * Copyright 2010 JBoss Inc
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

package org.drools.repository;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implements the Iterator interface, allowing iteration over the version history of versionableItem 
 * nodes
 * 
 * @author btruitt
 */
class ItemVersionIterator implements Iterator {
    private static final Logger log = LoggerFactory.getLogger(ItemVersionIterator.class);
    
    private VersionableItem currentVersionableItem;
    private int iterationType;
    
    public static final int ITERATION_TYPE_SUCCESSOR = 1;
    public static final int ITERATION_TYPE_PREDECESSOR = 2;

    public ItemVersionIterator(VersionableItem versionableItem, int iterationType) {
        this.currentVersionableItem = versionableItem;
        this.iterationType = iterationType;
    }
    
    public boolean hasNext() {
        if(this.currentVersionableItem == null) {
            return false;
        }
        
        if(this.iterationType == ITERATION_TYPE_SUCCESSOR) {
            return (this.currentVersionableItem.getSucceedingVersion() != null);
        }
        else if(this.iterationType == ITERATION_TYPE_PREDECESSOR) {
            return (this.currentVersionableItem.getPrecedingVersion() != null);
        }
        else {
            //shouldn't reach this block
            log.error("Reached unexpected path of execution because iterationType is set to: " + this.iterationType);
            return false;
        }
    }

    public Object next() {
        if(this.iterationType == ITERATION_TYPE_SUCCESSOR) {
            this.currentVersionableItem = this.currentVersionableItem.getSucceedingVersion();            
        }
        else if(this.iterationType == ITERATION_TYPE_PREDECESSOR) {
            this.currentVersionableItem = this.currentVersionableItem.getPrecedingVersion();
        }
        else {
            //shouldn't reach this block
            log.error("Reached unexpected path of execution because iterationType is set to: " + this.iterationType);
            return null;
        }
        
        if(this.currentVersionableItem == null) {
            throw new NoSuchElementException();
        }
        return this.currentVersionableItem;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
