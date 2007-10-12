package org.drools.testframework;


public class TestScenarioCriteria {


    /**
     * This ensures that these rules are NOT fired - all other rules may be fired.
     */
    public String[] rulesToExclude;


    /**
     * The list of rules that may be fired (all other activations are ignored).
     */
    public String[] rulesToInclude;


    /**
     * No rules will fire.
     */
    public boolean preventAllFiring = false;



    /**
     * Activation counts. ?? do I need to count firing versus activations ??????
     */



    /**
     * Collect and show statistics.
     */
    boolean collectStats = false;


}
