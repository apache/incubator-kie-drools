package org.drools.agent;

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

    /**
     * Perform the scan, adding in any packages changed to the rulebase.
     * If there was an error reading the packages, this will not fail, it will 
     * just do nothing (as there may be a temporary IO issue). 
     */
    abstract void updateRuleBase(RuleBase rb,
                                 boolean removeExistingPackages);

    /**
     * This will be passed the entire config.
     */
    abstract void configure(Properties config);

    /**
     * Remove the package from the rulebase if it exists in it.
     * If it does not, does nothing.
     */
    void removePackage(String name,
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
    
    
    void applyChanges(RuleBase rb, boolean removeExistingPackages, Package[] changes) {
        if ( changes == null ) return;
        for ( int i = 0; i < changes.length; i++ ) {
            Package p = changes[i];
            if ( removeExistingPackages ) {
                removePackage( p.getName(),
                               rb );
            }
            try {
                rb.addPackage( p );
            } catch ( Exception e ) {
                throw new RuntimeDroolsException( e );
            }
        }
    }
    
    
}
