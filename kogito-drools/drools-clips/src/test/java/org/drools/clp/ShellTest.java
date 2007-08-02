package org.drools.clp;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.rule.Package;
import org.drools.rule.Rule;

public class ShellTest extends TestCase {
    public void test1() {
        Shell shell = new Shell();
        shell.evalString( "(defrule xxx => )" );
        
        Package pkg = shell.getWorkingMemory().getRuleBase().getPackage( "MAIN" );
        //Rule rule = pkg.getRule( "xxx" );
    }
}
