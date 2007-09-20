package org.drools.rule.builder.dialect.java;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.drools.Cheese;
import org.drools.Person;
import org.drools.base.ClassFieldExtractorCache;
import org.drools.base.ClassObjectType;
import org.drools.rule.Declaration;
import org.drools.spi.PatternExtractor;
import org.drools.util.StringUtils;
import org.mvel.MVELTemplateRegistry;
import org.mvel.TemplateInterpreter;
import org.mvel.TemplateRegistry;

public class AccumulateTemplateTest extends TestCase {

    protected void setUp() throws Exception {
        super.setUp();
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testMethodGeneration() {
        final String className = "accumulate0";

        final String[] declarationTypes = new String[]{"String", "int"};
        final Declaration[] declarations = new Declaration[]{new Declaration( "name",
                                                                              null,
                                                                              null ), new Declaration( "age",
                                                                                                       null,
                                                                                                       null )};
        final Declaration[] inner = new Declaration[]{new Declaration( "cheese",
                                                                       new PatternExtractor( new ClassObjectType( Cheese.class ) ),
                                                                       null ), new Declaration( "price",
                                                                                                ClassFieldExtractorCache.getExtractor( Cheese.class,
                                                                                                                                       "price",
                                                                                                                                       getClass().getClassLoader() ),
                                                                                                null )};
        final String[] globals = new String[]{"aGlobal", "anotherGlobal"};
        final List globalTypes = Arrays.asList( new String[]{"String", "String"} );

        final Map map = new HashMap();

        map.put( "className",
                 StringUtils.ucFirst( className ) );

        map.put( "instanceName",
                 className );

        map.put( "package",
                 "org.drools" );

        map.put( "ruleClassName",
                 "Rule0" );

        map.put( "invokerClassName",
                 "Rule0" + StringUtils.ucFirst( className ) + "Invoker" );

        map.put( "declarations",
                 declarations );

        map.put( "declarationTypes",
                 declarationTypes );

        map.put( "globals",
                 globals );

        map.put( "globalTypes",
                 globalTypes );

        map.put( "innerDeclarations",
                 inner );

        map.put( "attributes",
                 new String[]{"x"} );
        map.put( "attributesTypes",
                 new String[]{"int"} );
        map.put( "initCode",
                 "x = 0;" );
        map.put( "actionCode",
                 "x += 1;" );
        map.put( "reverseCode",
                 "x -= 1;" );
        map.put( "resultCode",
                 "x + 10" );
        map.put( "supportsReverse",
                 "true" );

        map.put( "resultType",
                 Integer.class );

        map.put( "hashCode",
                 new Integer( 10 ) );

        TemplateRegistry registry = getRuleTemplateRegistry();
        Object method = TemplateInterpreter.parse( registry.getTemplate( "accumulateInnerClass" ),
                                                   null,
                                                   map,
                                                   registry );

        //System.out.println( method );
    }

    public void testInvokerGenerationSinglePattern() {
        final String className = "accumulate0";

        final String[] declarationTypes = new String[]{"String", "int"};
        final Declaration[] declarations = new Declaration[]{new Declaration( "name",
                                                                              ClassFieldExtractorCache.getExtractor( Person.class,
                                                                                                                     "name",
                                                                                                                     getClass().getClassLoader() ),
                                                                              null ), new Declaration( "age",
                                                                                                       ClassFieldExtractorCache.getExtractor( Person.class,
                                                                                                                                              "age",
                                                                                                                                              getClass().getClassLoader() ),
                                                                                                       null )};
        final Declaration[] inner = new Declaration[]{new Declaration( "cheese",
                                                                       new PatternExtractor( new ClassObjectType( Cheese.class ) ),
                                                                       null ), new Declaration( "price",
                                                                                                ClassFieldExtractorCache.getExtractor( Cheese.class,
                                                                                                                                       "price",
                                                                                                                                       getClass().getClassLoader() ),
                                                                                                null )};
        final String[] globals = new String[]{"aGlobal", "anotherGlobal"};
        final List globalTypes = Arrays.asList( new String[]{"String", "String"} );

        final Map map = new HashMap();

        map.put( "className",
                 StringUtils.ucFirst( className ) );

        map.put( "instanceName",
                 className );

        map.put( "package",
                 "org.drools" );

        map.put( "ruleClassName",
                 "Rule0" );

        map.put( "invokerClassName",
                 "Rule0" + StringUtils.ucFirst( className ) + "Invoker" );

        map.put( "declarations",
                 declarations );

        map.put( "declarationTypes",
                 declarationTypes );

        map.put( "globals",
                 globals );

        map.put( "globalTypes",
                 globalTypes );

        map.put( "innerDeclarations",
                 inner );

        map.put( "attributes",
                 new Attribute[]{new Attribute( "int",
                                                "x" )} );
        map.put( "initCode",
                 "x = 0;" );
        map.put( "actionCode",
                 "x += 1;" );
        map.put( "reverseCode",
                 "" );
        map.put( "resultCode",
                 "x + 10" );

        map.put( "supportsReverse",
                 "false" );

        map.put( "resultType",
                 Integer.class );

        map.put( "hashCode",
                 new Integer( 10 ) );
        map.put( "isMultiPattern", Boolean.FALSE );

        TemplateRegistry registry = getInvokerTemplateRegistry();
        Object method = TemplateInterpreter.parse( registry.getTemplate( "accumulateInvoker" ),
                                                   null,
                                                   map,
                                                   registry );

        //System.out.println( method );
    }

    public void testInvokerGenerationMultiPattern() {
        final String className = "accumulate0";

        final String[] declarationTypes = new String[]{"String", "int"};
        final Declaration[] declarations = new Declaration[]{new Declaration( "name",
                                                                              ClassFieldExtractorCache.getExtractor( Person.class,
                                                                                                                     "name",
                                                                                                                     getClass().getClassLoader() ),
                                                                              null ), new Declaration( "age",
                                                                                                       ClassFieldExtractorCache.getExtractor( Person.class,
                                                                                                                                              "age",
                                                                                                                                              getClass().getClassLoader() ),
                                                                                                       null )};
        final Declaration[] inner = new Declaration[]{new Declaration( "$cheese",
                                                                       new PatternExtractor( new ClassObjectType( Cheese.class ) ),
                                                                       null ), new Declaration( "$person",
                                                                                                new PatternExtractor( new ClassObjectType ( Person.class ) ),
                                                                                                null ) };
        final String[] globals = new String[]{"aGlobal", "anotherGlobal"};
        final List globalTypes = Arrays.asList( new String[]{"String", "String"} );

        final Map map = new HashMap();

        map.put( "className",
                 StringUtils.ucFirst( className ) );

        map.put( "instanceName",
                 className );

        map.put( "package",
                 "org.drools" );

        map.put( "ruleClassName",
                 "Rule0" );

        map.put( "invokerClassName",
                 "Rule0" + StringUtils.ucFirst( className ) + "Invoker" );

        map.put( "declarations",
                 declarations );

        map.put( "declarationTypes",
                 declarationTypes );

        map.put( "globals",
                 globals );

        map.put( "globalTypes",
                 globalTypes );

        map.put( "innerDeclarations",
                 inner );

        map.put( "attributes",
                 new Attribute[]{new Attribute( "int",
                                                "x" )} );
        map.put( "initCode",
                 "x = 0;" );
        map.put( "actionCode",
                 "x += 1;" );
        map.put( "reverseCode",
                 "" );
        map.put( "resultCode",
                 "x + 10" );

        map.put( "supportsReverse",
                 "false" );

        map.put( "resultType",
                 Integer.class );

        map.put( "hashCode",
                 new Integer( 10 ) );
        map.put( "isMultiPattern", Boolean.TRUE );

        TemplateRegistry registry = getInvokerTemplateRegistry();
        Object method = TemplateInterpreter.parse( registry.getTemplate( "accumulateInvoker" ),
                                                   null,
                                                   map,
                                                   registry );

        //System.out.println( method );
    }

    private TemplateRegistry getRuleTemplateRegistry() {
        TemplateRegistry ruleRegistry = new MVELTemplateRegistry();
        ruleRegistry.registerTemplate( new InputStreamReader( AbstractJavaBuilder.class.getResourceAsStream( "javaRule.mvel" ) ) );

        return ruleRegistry;
    }

    private TemplateRegistry getInvokerTemplateRegistry() {
        TemplateRegistry invokerRegistry = new MVELTemplateRegistry();
        invokerRegistry.registerTemplate( new InputStreamReader( AbstractJavaBuilder.class.getResourceAsStream( "javaInvokers.mvel" ) ) );

        return invokerRegistry;
    }

    public static class Attribute {
        private String type;
        private String name;

        public Attribute(String type,
                         String name) {
            this.type = type;
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

}
