package org.drools.semantics.java;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.AngleBracketTemplateLexer;
import org.drools.Cheese;
import org.drools.Person;
import org.drools.base.ClassFieldExtractor;
import org.drools.base.ClassObjectType;
import org.drools.rule.Declaration;
import org.drools.rule.builder.RuleBuilder;
import org.drools.spi.ColumnExtractor;

public class AccumulateTemplateTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testMethodGeneration() {
        StringTemplateGroup ruleGroup = new StringTemplateGroup( new InputStreamReader( RuleBuilder.class.getResourceAsStream( "javaRule.stg" ) ),
                                                                 AngleBracketTemplateLexer.class );
        StringTemplate accMethod = ruleGroup.getInstanceOf( "accumulateMethod" );

        final String[] declarationTypes = new String[]{"String", "int"};
        final Declaration[] declarations = new Declaration[]{new Declaration( "name",
                                                                              null,
                                                                              null ), 
                                                             new Declaration( "age",
                                                                              null,
                                                                              null )};
        final Declaration[] inner = new Declaration[]{new Declaration( "cheese",
                                                                              new ColumnExtractor(new ClassObjectType(Cheese.class)),
                                                                              null ), 
                                                      new Declaration( "price",
                                                                              new ClassFieldExtractor(Cheese.class, "price"),
                                                                              null )};
        final String[] globals = new String[]{"aGlobal", "anotherGlobal"};
        final List globalTypes = Arrays.asList( new String[]{"String", "String"} );

        accMethod.setAttribute( "declarations",
                                declarations );
        accMethod.setAttribute( "declarationTypes",
                                declarationTypes );
        accMethod.setAttribute( "innerDeclarations",
                                inner );
        accMethod.setAttribute( "globals",
                                globals );
        accMethod.setAttribute( "globalTypes",
                                globalTypes );
        accMethod.setAttribute( "methodName",
                                "accumulateTestMethod" );
        accMethod.setAttribute( "columnType",
                                "MyClass" );
        accMethod.setAttribute( "columnDeclaration",
                                "$myclass" );
        accMethod.setAttribute( "initCode",
                                "int x = 0;" );
        accMethod.setAttribute( "actionCode",
                                "x += 1;" );
        accMethod.setAttribute( "resultCode",
                                "x + 10" );

        System.out.println( accMethod.toString() );
    }

    public void testInvokerGeneration() {
        StringTemplateGroup ruleGroup = new StringTemplateGroup( new InputStreamReader( RuleBuilder.class.getResourceAsStream( "javaInvokers.stg" ) ),
                                                                 AngleBracketTemplateLexer.class );
        StringTemplate accMethod = ruleGroup.getInstanceOf( "accumulateInvoker" );

        final String[] declarationTypes = new String[]{"String", "int"};
        final Declaration[] declarations = new Declaration[]{new Declaration( "name",
                                                                              new ClassFieldExtractor(Person.class, "name"),
                                                                              null ), new Declaration( "age",
                                                                                                       new ClassFieldExtractor(Person.class, "age"),
                                                                                                       null )};
        final String[] globals = new String[]{"aGlobal", "anotherGlobal"};
        final List globalTypes = Arrays.asList( new String[]{"String", "String"} );

        accMethod.setAttribute( "declarations",
                                declarations );
        accMethod.setAttribute( "declarationTypes",
                                declarationTypes );
        accMethod.setAttribute( "globals",
                                globals );
        accMethod.setAttribute( "globalTypes",
                                globalTypes );
        accMethod.setAttribute( "package",
                                "org.drools.semantics.java" );
        accMethod.setAttribute( "invokerClassName",
                                "AccumulateInvokerClass" );
        accMethod.setAttribute( "ruleClassName",
                                "RuleWithAccumulate" );
        accMethod.setAttribute( "methodName",
                                "accumulateTestMethod" );
        accMethod.setAttribute( "hashCode",
                                new Integer(13) );

        System.out.println( accMethod.toString() );
    }
}
