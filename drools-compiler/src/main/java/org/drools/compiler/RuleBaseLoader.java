package org.drools.compiler;

import java.io.IOException;
import java.io.Reader;

import org.drools.CheckedDroolsException;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.rule.Package;

/**
 * A high level helper class for creating rulebases from source.
 * For additional control, refer to the DrlParser and PackageBuilder classes.
 * You can have much more detailed control with the lower level classes.
 * 
 * @author Michael Neale
 */
public class RuleBaseLoader {

    private static RuleBaseLoader INSTANCE = new RuleBaseLoader();
    private static int engineType = RuleBase.RETEOO;
    
    private RuleBaseLoader() {
    }
    
    /** Get an instance of the loader */
    public static RuleBaseLoader getInstance() {
        return INSTANCE;
    }
    
    /**
     * Create a new RuleBase from the drl source. 
     * Uses the current default engine type.
     */
    public RuleBase loadFromReader(Reader drl) throws CheckedDroolsException, IOException  {
        PackageBuilder builder = new PackageBuilder(  );
        builder.addPackageFromDrl( drl );
        return makeRuleBase( builder );
    }
    
    /**
     * Create a new RuleBase from the drl and dsl source.
     * Uses the current default engine type.
     */
    public RuleBase loadFromReader(Reader drl, Reader dsl) throws CheckedDroolsException, IOException  {
        PackageBuilder builder = new PackageBuilder(  );
        builder.addPackageFromDrl( drl, dsl );
        return makeRuleBase( builder );
    }    

    /** Replace the default engine type that will be used (default is RETEOO) */
    public void setDefaultEngineType(int type) {
        engineType = type;
    }
    
    private RuleBase makeRuleBase(PackageBuilder builder) throws CheckedDroolsException {
        if (builder.hasErrors()) {
            throw new CheckedDroolsException("There were errors in the rule source: " + builder.printErrors());
        }
        Package binaryPackage = builder.getPackage();
        
        RuleBase rb = RuleBaseFactory.newRuleBase(engineType);
        try {
            rb.addPackage( binaryPackage );
        } catch ( Exception e ) {
            throw new CheckedDroolsException("Unable to add compiled package to rulebase. Nested error is: " + e.getMessage());
        }
        return rb;
    }
    

    
}
