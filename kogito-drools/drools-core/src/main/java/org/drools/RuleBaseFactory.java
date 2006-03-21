package org.drools;

/**
 * This is a utility to create rule bases based on the type of engine you wish to use.
 * 
 * @author Michael Neale
 */
public class RuleBaseFactory {

    public static final int RETEOO = 1;
    public static final int LEAPS = 2;
    
    
    private static RuleBaseFactory INSTANCE = new RuleBaseFactory();
    
    private RuleBaseFactory() {
    }
    
    public static RuleBaseFactory getInstance() {
        return INSTANCE;
    }
    
    /** Create a new default rule base (RETEOO type engine) */
    public RuleBase newRuleBase() {
        return newRuleBase(RETEOO);
    }

    /** Create a new RuleBase of the appropriate type */
    public RuleBase newRuleBase(int type) {
        switch ( type ) {
            case RETEOO :
                return new org.drools.reteoo.RuleBaseImpl();
            case LEAPS :
                try {
                    return new org.drools.leaps.RuleBaseImpl();
                } catch ( PackageIntegrationException e ) {
                    throw new IllegalStateException("Unable to create Leaps engine. Error: " + e.getMessage());
                }
            default :
                throw new IllegalArgumentException("Unknown engine type: " + type);
                
        }
    }
    
    
    
    
    
}
