package org.acme.insurance.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.HashMap;

import junit.framework.TestCase;

import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.WorkingMemory;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.PackageBuilder;
import org.drools.lang.descr.PackageDescr;
import org.drools.rule.Package;
import org.mvel.MVEL;

public class MVELTest extends TestCase {
    public void testHelloWorld() throws Exception {

        String tempFile = "/Users/michaelneale/foo3.pkg";

        // read in the source

        final Reader reader = new InputStreamReader( getClass().getResourceAsStream( "mvel_test.drl" ) );




 /* uncomment the block below to create a binary package to use the next time you run it */
//        Package pkg = loadPackage( reader );
//
//
//
//        FileOutputStream out = new FileOutputStream(tempFile);
//        ObjectOutputStream obj = new ObjectOutputStream( out );
//        obj.writeObject( pkg );
//        obj.close();



        ObjectInputStream in = new ObjectInputStream( new FileInputStream(tempFile) );
        Package pkg2 = (Package) in.readObject();
        // end of serialization block


        RuleBase ruleBase = RuleBaseFactory.newRuleBase();

        ruleBase.addPackage( pkg2 );

        final WorkingMemory workingMemory = ruleBase.newStatefulSession();







        workingMemory.insert( new Driver() );

        workingMemory.fireAllRules();

    }





    private Package loadPackage(final Reader reader) throws DroolsParserException, Exception {

        PackageBuilder builder = new PackageBuilder();
        builder.addPackage( new PackageDescr("org.acme.insuranceXXS") );
        builder.addPackageFromDrl( new StringReader("import org.acme.insurance.base.Approve\n" +
                "import org.acme.insurance.base.Driver")  );

        builder.addPackageFromDrl( reader );



        final Package pkg = builder.getPackage();
        //assertTrue(pkg.isValid());
        return pkg;
    }

    protected RuleBase getRuleBase() throws Exception {

        return RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                            null );
    }
}
