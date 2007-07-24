package org.drools.examples;

import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.WorkingMemory;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;

public class ShoppingDiscount {

    public static final void main(String[] args) throws Exception {
        final PackageBuilder builder = new PackageBuilder();
        builder.addPackageFromDrl( new InputStreamReader( ShoppingDiscount.class.getResourceAsStream( "Shopping.drl" ) ) );

        final RuleBase ruleBase = RuleBaseFactory.newRuleBase();
        ruleBase.addPackage( builder.getPackage() );

        final StatefulSession session = ruleBase.newStatefulSession();

        Customer mark = new Customer( "mark",
                                      0 );
        session.insert( mark );

        Product shoes = new Product( "shoes",
                                     60 );
        session.insert( shoes );

        Product hat = new Product( "hat",
                                   60 );
        session.insert( hat );

        session.insert( new Purchase( mark,
                                      shoes ) );
        FactHandle hatPurchaseHandle = session.insert( new Purchase( mark,
                                                                     hat ) );

        session.fireAllRules();

        session.retract( hatPurchaseHandle );
        System.out.println( "Customer mark has returned the hat" );
        session.fireAllRules();
    }

}
