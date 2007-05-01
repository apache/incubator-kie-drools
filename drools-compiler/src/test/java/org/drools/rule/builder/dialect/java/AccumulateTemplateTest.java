package org.drools.rule.builder.dialect.java;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.Person;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassObjectType;
import org.drools.rule.Declaration;
import org.drools.spi.PatternExtractor;

public class AccumulateTemplateTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }
    
    public void testEmpty() {
        
    }

//    public void testMethodGeneration() {
//        final StringTemplateGroup ruleGroup = new StringTemplateGroup( new InputStreamReader( AccumulateTemplateTest.class.getResourceAsStream( "javaRule.stg" ) ),
//                                                                 AngleBracketTemplateLexer.class );
//        final StringTemplate accMethod = ruleGroup.getInstanceOf( "accumulateMethod" );
//
//        final String[] declarationTypes = new String[]{"String", "int"};
//        final Declaration[] declarations = new Declaration[]{new Declaration( "name",
//                                                                              null,
//                                                                              null ), new Declaration( "age",
//                                                                                                       null,
//                                                                                                       null )};
//        final Declaration[] inner = new Declaration[]{new Declaration( "cheese",
//                                                                       new PatternExtractor( new ClassObjectType( Cheese.class ) ),
//                                                                       null ), new Declaration( "price",
//                                                                                                new ClassFieldExtractor( Cheese.class,
//                                                                                                                         "price" ),
//                                                                                                null )};
//        final String[] globals = new String[]{"aGlobal", "anotherGlobal"};
//        final List globalTypes = Arrays.asList( new String[]{"String", "String"} );
//
//        accMethod.setAttribute( "declarations",
//                                declarations );
//        accMethod.setAttribute( "declarationTypes",
//                                declarationTypes );
//        accMethod.setAttribute( "innerDeclarations",
//                                inner );
//        accMethod.setAttribute( "globals",
//                                globals );
//        accMethod.setAttribute( "globalTypes",
//                                globalTypes );
//        accMethod.setAttribute( "methodName",
//                                "accumulateTestMethod" );
//        accMethod.setAttribute( "patternType",
//                                "MyClass" );
//        accMethod.setAttribute( "patternDeclaration",
//                                "$myclass" );
//        accMethod.setAttribute( "initCode",
//                                "int x = 0;" );
//        accMethod.setAttribute( "actionCode",
//                                "x += 1;" );
//        accMethod.setAttribute( "resultCode",
//                                "x + 10" );
//
//        System.out.println( accMethod.toString() );
//    }
//
//    public void testInvokerGeneration() {
//        final StringTemplateGroup ruleGroup = new StringTemplateGroup( new InputStreamReader( AccumulateTemplateTest.class.getResourceAsStream( "javaInvokers.stg" ) ),
//                                                                 AngleBracketTemplateLexer.class );
//        final StringTemplate accMethod = ruleGroup.getInstanceOf( "accumulateInvoker" );
//
//        final String[] declarationTypes = new String[]{"String", "int"};
//        final Declaration[] declarations = new Declaration[]{new Declaration( "name",
//                                                                              new ClassFieldExtractor( Person.class,
//                                                                                                       "name" ),
//                                                                              null ), new Declaration( "age",
//                                                                                                       new ClassFieldExtractor( Person.class,
//                                                                                                                                "age" ),
//                                                                                                       null )};
//        final String[] globals = new String[]{"aGlobal", "anotherGlobal"};
//        final List globalTypes = Arrays.asList( new String[]{"String", "String"} );
//
//        accMethod.setAttribute( "declarations",
//                                declarations );
//        accMethod.setAttribute( "declarationTypes",
//                                declarationTypes );
//        accMethod.setAttribute( "globals",
//                                globals );
//        accMethod.setAttribute( "globalTypes",
//                                globalTypes );
//        accMethod.setAttribute( "package",
//                                "org.drools.semantics.java" );
//        accMethod.setAttribute( "invokerClassName",
//                                "AccumulateInvokerClass" );
//        accMethod.setAttribute( "ruleClassName",
//                                "RuleWithAccumulate" );
//        accMethod.setAttribute( "methodName",
//                                "accumulateTestMethod" );
//        accMethod.setAttribute( "hashCode",
//                                new Integer( 13 ) );
//
//        System.out.println( accMethod.toString() );
//    }
}
