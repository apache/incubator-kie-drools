/*
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

package org.drools.agent;

import java.util.Collection;
import java.util.Iterator;
import java.util.Properties;

import org.drools.RuleBase;
import org.drools.RuntimeDroolsException;
import org.drools.rule.Package;

/**
 * All sources of packages must implement this. 
 * @author Michael Neale
 *
 */
public abstract class PackageProvider {

    protected AgentEventListener listener;

    /**
     * Perform the scan, adding in any packages changed.
     * if there are no changes, null should be returned.
     * If there was an error reading the packages, this will not fail, it will 
     * just do nothing (as there may be a temporary IO issue). 
     */
    abstract PackageChangeInfo loadPackageChanges();

    /**
     * This will be passed the entire config.
     */
    abstract void configure(Properties config);

    /**
     * Remove the package from the rulebase if it exists in it.
     * If it does not, does nothing.
     */
    static void removePackage(String name,
                               RuleBase rb) {
        Package[] ps = rb.getPackages();
        if ( ps == null ) return;
        for ( int i = 0; i < ps.length; i++ ) {
            Package p = ps[i];
            if ( p.getName().equals( name ) ) {
                rb.removePackage( name );
                return;
            }
        }
    }

    static void removePackages(	Collection<String> removedPackages,
        						RuleBase rb,
        						AgentEventListener listener ) {

        for (String name : removedPackages) {
            listener.info( "Removing package called " + name );
            removePackage(name, rb);
        }
    }
    
    static void applyChanges(RuleBase rb, boolean removeExistingPackages, Collection changes, 
        	AgentEventListener listener) {
        applyChanges(rb, removeExistingPackages, changes, null, listener);
    }
    
    static void applyChanges(RuleBase rb, boolean removeExistingPackages, Collection changes, 
        						Collection<String> removed, AgentEventListener listener) {
        if ( changes == null && removed == null ) return;

        rb.lock();
        
        if(removed != null ) {
        	removePackages(removed, rb, listener);
        }

        if( changes != null ) {
            for ( Iterator iter = changes.iterator(); iter.hasNext(); ) {
                Package p = (Package) iter.next();

                if ( removeExistingPackages ) {
                    removePackage( p.getName(),
                                   rb );
                }
                try {
                    listener.info( "Adding package called " + p.getName() );
                    rb.addPackage( p );
                } catch ( Exception e ) {
                    throw new RuntimeDroolsException( e );
                }
            }
        }
        
        rb.unlock();
    }
    
    public void setAgentListener(AgentEventListener listener) {
        this.listener = listener;
    }
    
    
}
