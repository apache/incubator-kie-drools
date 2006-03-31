package org.drools.decisiontable;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;


import org.drools.Cheese;
import org.drools.Person;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;


import junit.framework.TestCase;

public class SpreadsheetIntegrationTest extends TestCase {

    
    
    public void testExecute() throws Exception {
        SpreadsheetCompiler converter = new SpreadsheetCompiler( );
        String drl = converter.compile( "/data/IntegrationExampleTest.xls", InputType.XLS );
        assertNotNull(drl);
        //System.out.println(drl);
        
        //COMPILE
        PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new StringReader(drl) );
        
        Package pkg = builder.getPackage();
        assertNotNull(pkg);
        assertEquals(0, builder.getErrors().length);
        
        //BUILD RULEBASE
        RuleBase rb = RuleBaseFactory.getInstance().newRuleBase();
        rb.addPackage( pkg );
        
        //NEW WORKING MEMORY
        WorkingMemory wm = rb.newWorkingMemory();
        
        //ASSERT AND FIRE
        wm.assertObject( new Cheese("stilton", 42) );
        wm.assertObject( new Person("michael","stilton", 42) );
        List list = new ArrayList();
        wm.setGlobal( "list", list );
        wm.fireAllRules();
        assertEquals(1, list.size());
        
    }
    
}
