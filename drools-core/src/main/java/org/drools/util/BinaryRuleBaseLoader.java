package org.drools.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.RuntimeDroolsException;
import org.drools.rule.Package;

/**
 * This loads up rulebases from binary packages.
 * Can work with an existing or a new rulebase.
 * This is useful for deployment.
 * 
 * @author Michael Neale
 */
public class BinaryRuleBaseLoader {

    private RuleBase ruleBase;

    /**
     * This will create a new default rulebase (which is initially empty).
     * 
     */
    public BinaryRuleBaseLoader() {
        this( RuleBaseFactory.newRuleBase() );
    }

    /**
     * This will add any binary packages to the rulebase.
     */
    public BinaryRuleBaseLoader(
                                RuleBase rb) {
        this.ruleBase = rb;
    }

    /**
     * This will add the BINARY package to the rulebase.
     * @param in An input stream to the serialized package.
     */
    public void addPackage(InputStream in) {

        try {
            ObjectInputStream oin = new ObjectInputStream( in );
            Object opkg = oin.readObject();
            if (! (opkg instanceof Package)) {
                throw new IllegalArgumentException("Can only add instances of org.drools.rule.Package to a rulebase instance.");
            }
            Package binPkg = (Package) opkg;
            
            if ( !binPkg.isValid() ) {
                throw new IllegalArgumentException( "Can't add a non valid package to a rulebase." );
            }

            try {
                this.ruleBase.addPackage( binPkg );
            } catch ( Exception e ) {
                throw new RuntimeDroolsException( "Unable to add package to the rulebase.",
                                                  e );
            }

        } catch ( IOException e ) {
            throw new RuntimeDroolsException( e );
        } catch ( ClassNotFoundException e ) {
            throw new RuntimeDroolsException( e );
        } finally {
            try {
                in.close();
            } catch ( IOException e ) {
                throw new RuntimeException( e );
            }
        }

    }

    public RuleBase getRuleBase() {
        return this.ruleBase;
    }

}
