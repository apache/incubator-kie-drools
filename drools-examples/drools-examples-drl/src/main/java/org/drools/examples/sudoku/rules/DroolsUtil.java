/*
 * JBoss, the OpenSource J2EE webOS
 * 
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 */
package org.drools.examples.sudoku.rules;

import java.io.InputStreamReader;
import java.io.Reader;

import org.drools.RuleBase;
import org.drools.RuleBaseConfiguration;
import org.drools.RuleBaseFactory;
import org.drools.compiler.PackageBuilder;
import org.drools.rule.Package;

public class DroolsUtil
{
   private static DroolsUtil INSTANCE;
   
   private DroolsUtil()
   {
      
   }
   
   public static DroolsUtil getInstance()
   {
      if (INSTANCE == null)
      {
         INSTANCE = new DroolsUtil();
      }
      
      return INSTANCE;
   }
   
   public RuleBase readRuleBase(String drlFileName) 
      throws Exception 
   {
      //read in the source
      Reader source = new InputStreamReader( DroolsSudokuGridModel.class.getResourceAsStream(drlFileName) );
   
      //optionally read in the DSL (if you are using it).
      //Reader dsl = new InputStreamReader( DroolsTest.class.getResourceAsStream( "/mylang.dsl" ) );
   
      //Use package builder to build up a rule package.
      //An alternative lower level class called "DrlParser" can also be used...
   
      PackageBuilder builder = new PackageBuilder();
   
      //this will parse and compile in one step
      //NOTE: There are 2 methods here, the one argument one is for normal DRL.
      builder.addPackageFromDrl( source );
   
      //Use the following instead of above if you are using a DSL:
      //builder.addPackageFromDrl( source, dsl );
   
      //get the compiled package (which is serializable)
      Package pkg = builder.getPackage();
   
      //add the package to a rulebase (deploy the rule package).
      RuleBaseConfiguration conf = new RuleBaseConfiguration();
      conf.setRemoveIdentities( true );
      RuleBase ruleBase = RuleBaseFactory.newRuleBase( RuleBase.RETEOO,
                                                       conf );
      ruleBase.addPackage( pkg );
      return ruleBase;
   }   
}
