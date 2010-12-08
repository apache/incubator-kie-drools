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

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * The item class is used to abstract away the underlying details of the JCR repository.
 * 
 * @author btruitt
 */
public abstract class Item {
    static Logger log = LoggerFactory.getLogger(Item.class);

    /**
     * The node within the repository that this item corresponds to
     */
    protected Node node;
    
    /**
     * The RulesRepository object that this object was created from
     */
    protected RulesRepository rulesRepository;

    /**
     * Sets the item object's node attribute to the specified node
     * 
     * @param rulesRepository the RulesRepository object that this object is being created from
     * @param node the node in the repository that this item corresponds to
     */
    public Item(RulesRepository rulesRepository, Node node) {
        this.rulesRepository = rulesRepository;
        this.node = node;
    }

    /**
     * gets the node in the repository that this item is associated with
     * 
     * @return the node in the repository that this item is associated with
     */
    public Node getNode() {
        return node;
    }    
    
    /**
     * gets the name of this item (unless overridden in a subclass, this just returns the
     * name of the node that this Item encapsulates.
     * 
     * @return the name of the node that this item encapsultes
     * @throws RulesRepositoryException 
     */
    public String getName() throws RulesRepositoryException {
        try {
            return this.node.getName();
        }
        catch(Exception e) {
            log.error("Caught exception: " + e);
            throw new RulesRepositoryException(e);
        }
    }

    /**
     * @return the RulesRepository object that this object was instantiated from
     */
    public RulesRepository getRulesRepository() {
        return rulesRepository;
    }
    
    public boolean equals(Object obj) {
        if(!(obj instanceof Item)) {
            return false;
        }
        else {
            Item rhs = (Item)obj;
            try {
				return this.node.isSame(rhs.getNode());
			} catch (RepositoryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
        }
    }

    public int hashCode() {
        return this.node.hashCode();
    }        
    

}
