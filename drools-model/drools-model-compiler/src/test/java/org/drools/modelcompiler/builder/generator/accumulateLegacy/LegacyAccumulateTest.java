package org.drools.modelcompiler.builder.generator.accumulateLegacy;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.base.ClassObjectType;
import org.drools.core.rule.Declaration;
import org.drools.core.spi.PatternExtractor;
import org.drools.core.util.StringUtils;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.CompilationUnit;
import org.drools.modelcompiler.domain.Person;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class LegacyAccumulateTest {

    @Before
    public void setUp() throws Exception {
        store.setClassFieldAccessorCache( new ClassFieldAccessorCache(Thread.currentThread().getContextClassLoader() ) );
        store.setEagerWire( true );
    }

    ClassFieldAccessorStore store = new ClassFieldAccessorStore();

    public String multiPatternResult =
            "package org.drools;\n" +
            "\n" +
            "import org.mvel2.asm.ClassReader;\n" +
            "import org.mvel2.asm.util.TraceMethodVisitor;\n" +
            "import org.drools.core.util.asm.MethodComparator.Tracer;\n" +
            "import java.util.Collections;\n" +
            "public class Rule0Accumulate0Invoker implements org.drools.core.spi.Accumulator, org.drools.core.spi.CompiledInvoker\n" +
            "{\n" +
            "    private static final long serialVersionUID  = 510l;\n" +
            "\n" +
            "    public java.io.Serializable createContext() {\n" +
            "        return new Rule0.Accumulate0();\n" +
            "    }\n" +
            "\n" +
            "    public void init(java.lang.Object workingMemoryContext,\n" +
            "                     java.lang.Object context,\n" +
            "                     org.drools.core.spi.Tuple tuple,\n" +
            "                     org.drools.core.rule.Declaration[] declarations,\n" +
            "                     org.drools.core.WorkingMemory workingMemory) throws Exception {\n" +
            "         java.lang.String name = ( java.lang.String ) declarations[0].getValue( (org.drools.core.common.InternalWorkingMemory) workingMemory, tuple.getObject( declarations[0] ) );\n" +
            "            \n" +
            "         int age = ( java.lang.Integer ) declarations[1].getIntValue( (org.drools.core.common.InternalWorkingMemory) workingMemory, tuple.getObject( declarations[1] ) );\n" +
            "            \n" +
            "        \n" +
            "         String aGlobal = ( String ) workingMemory.getGlobal( \"aGlobal\" );\n" +
            "         String anotherGlobal = ( String ) workingMemory.getGlobal( \"anotherGlobal\" );\n" +
            "        \n" +
            "\n" +
            "        ((Rule0.Accumulate0)context).init(\n" +
            "             name, age,\n" +
            "             aGlobal, anotherGlobal );\n" +
            "\n" +
            "    }\n" +
            "\n" +
            "    public void accumulate(java.lang.Object workingMemoryContext,\n" +
            "                           java.lang.Object context,\n" +
            "                           org.drools.core.spi.Tuple tuple,\n" +
            "                           org.drools.core.common.InternalFactHandle handle,\n" +
            "                           org.drools.core.rule.Declaration[] declarations,\n" +
            "                           org.drools.core.rule.Declaration[] innerDeclarations,\n" +
            "                           org.drools.core.WorkingMemory workingMemory) throws Exception {\n" +
            "         java.lang.String name = ( java.lang.String ) declarations[0].getValue( (org.drools.core.common.InternalWorkingMemory) workingMemory, tuple.getObject( declarations[0] ) );\n" +
            "            \n" +
            "         int age = ( java.lang.Integer ) declarations[1].getIntValue( (org.drools.core.common.InternalWorkingMemory) workingMemory, tuple.getObject( declarations[1] ) );\n" +
            "            \n" +
            "        \n" +
            "         String aGlobal = ( String ) workingMemory.getGlobal( \"aGlobal\" );\n" +
            "         String anotherGlobal = ( String ) workingMemory.getGlobal( \"anotherGlobal\" );\n" +
            "        \n" +
            "        \n" +
            "           org.drools.compiler.Cheese $cheese = (org.drools.compiler.Cheese) innerDeclarations[0].getValue( (org.drools.core.common.InternalWorkingMemory) workingMemory, tuple.getObject( innerDeclarations[0] ) );\n" +
            "            \n" +
            "           org.drools.compiler.Person $person = (org.drools.compiler.Person) innerDeclarations[1].getValue( (org.drools.core.common.InternalWorkingMemory) workingMemory, tuple.getObject( innerDeclarations[1] ) );\n" +
            "            \n" +
            "          \n" +
            "        \n" +
            "        ((Rule0.Accumulate0)context).accumulate(\n" +
            "            workingMemory,\n" +
            "            handle,\n" +
            "            innerDeclarations,\n" +
            "            handle.getObject(),\n" +
            "             name, age,\n" +
            "             aGlobal, anotherGlobal,\n" +
            "             $cheese, $person);\n" +
            "    }\n" +
            "\n" +
            "    public void reverse(java.lang.Object workingMemoryContext,\n" +
            "                           java.lang.Object context,\n" +
            "                           org.drools.core.spi.Tuple tuple,\n" +
            "                           org.drools.core.common.InternalFactHandle handle,\n" +
            "                           org.drools.core.rule.Declaration[] declarations,\n" +
            "                           org.drools.core.rule.Declaration[] innerDeclarations,\n" +
            "                           org.drools.core.WorkingMemory workingMemory) throws Exception {\n" +
            "         java.lang.String name = ( java.lang.String ) declarations[0].getValue( (org.drools.core.common.InternalWorkingMemory) workingMemory, tuple.getObject( declarations[0] ) );\n" +
            "            \n" +
            "         int age = ( java.lang.Integer ) declarations[1].getIntValue( (org.drools.core.common.InternalWorkingMemory) workingMemory, tuple.getObject( declarations[1] ) );\n" +
            "            \n" +
            "        \n" +
            "         String aGlobal = ( String ) workingMemory.getGlobal( \"aGlobal\" );\n" +
            "         String anotherGlobal = ( String ) workingMemory.getGlobal( \"anotherGlobal\" );\n" +
            "        \n" +
            "        \n" +
            "           org.drools.compiler.Cheese $cheese = (org.drools.compiler.Cheese) innerDeclarations[0].getValue( (org.drools.core.common.InternalWorkingMemory) workingMemory, tuple.getObject( innerDeclarations[0] ) );\n" +
            "            \n" +
            "           org.drools.compiler.Person $person = (org.drools.compiler.Person) innerDeclarations[1].getValue( (org.drools.core.common.InternalWorkingMemory) workingMemory, tuple.getObject( innerDeclarations[1] ) );\n" +
            "            \n" +
            "          \n" +
            "        \n" +
            "\n" +
            "        ((Rule0.Accumulate0)context).reverse(\n" +
            "            workingMemory,\n" +
            "            handle,\n" +
            "            handle.getObject(),\n" +
            "             aGlobal, anotherGlobal);\n" +
            "    }\n" +
            "\n" +
            "    public Object getResult(java.lang.Object workingMemoryContext,\n" +
            "                            java.lang.Object context,\n" +
            "                            org.drools.core.spi.Tuple tuple,\n" +
            "                            org.drools.core.rule.Declaration[] declarations,\n" +
            "                            org.drools.core.WorkingMemory workingMemory) throws Exception {\n" +
            "         java.lang.String name = ( java.lang.String ) declarations[0].getValue( (org.drools.core.common.InternalWorkingMemory) workingMemory, tuple.getObject( declarations[0] ) );\n" +
            "            \n" +
            "         int age = ( java.lang.Integer ) declarations[1].getIntValue( (org.drools.core.common.InternalWorkingMemory) workingMemory, tuple.getObject( declarations[1] ) );\n" +
            "            \n" +
            "        \n" +
            "         String aGlobal = ( String ) workingMemory.getGlobal( \"aGlobal\" );\n" +
            "         String anotherGlobal = ( String ) workingMemory.getGlobal( \"anotherGlobal\" );\n" +
            "        \n" +
            "\n" +
            "        return ((Rule0.Accumulate0)context).getResult(\n" +
            "             name, age,\n" +
            "             aGlobal, anotherGlobal );\n" +
            "    }\n" +
            "\n" +
            "    public boolean supportsReverse() {\n" +
            "        return false;\n" +
            "    }\n" +
            "\n" +
            "    public Object createWorkingMemoryContext() {\n" +
            "        return null;\n" +
            "    }\n" +
            "\n" +
            "    \n" +
            "    public int hashCode() {\n" +
            "        return 10;\n" +
            "    }\n" +
            "\n" +
            "\n" +
            "    \n" +
            "    public boolean equals(Object object) {\n" +
            "        if ( object == null || !(object instanceof  org.drools.core.spi.CompiledInvoker) ) {\n" +
            "            return false;\n" +
            "        }\n" +
            "        return org.drools.core.util.asm.MethodComparator.compareBytecode( getMethodBytecode(), (( org.drools.core.spi.CompiledInvoker ) object).getMethodBytecode() );\n" +
            "    }\n" +
            "\n" +
            "\n" +
            "    public java.util.List getMethodBytecode() {\n" +
            "        java.io.InputStream is = Rule0.class.getClassLoader().getResourceAsStream( \"org.drools.Rule0\".replace( '.', '/' ) + \"$Accumulate0\" + \".class\" );\n" +
            "\n" +
            "        java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();\n" +
            "        byte[] data = new byte[1024];\n" +
            "        int byteCount;\n" +
            "        try {\n" +
            "            while ( (byteCount = is.read( data,\n" +
            "                                 0,\n" +
            "                                 1024 )) > -1 )\n" +
            "            {\n" +
            "                bos.write(data, 0, byteCount);\n" +
            "            }\n" +
            "        } catch ( java.io.IOException e ) {\n" +
            "            throw new RuntimeException(\"Unable getResourceAsStream for Class 'Rule0$Accumulate0' \");\n" +
            "        }\n" +
            "        return Collections.singletonList( bos );\n" +
            "    }\n" +
            "}";


    final CompilationUnit multiPatternResultParsed = JavaParser.parse(multiPatternResult);


    @Test
    public void testInvokerGenerationMultiPattern() throws Exception {

        final String className = "accumulate0";

        final String[] declarationTypes = new String[]{"String", "int"};
        final Declaration[] declarations = new Declaration[]{new Declaration("name",
                                                                             store.getReader( Person.class,
                                                                                              "name" ),
                                                                             null ), new Declaration( "age",
                                                                                                      store.getReader( Person.class,
                                                                                                                       "age" ),
                                                                                                      null )};
        final Declaration[] inner = new Declaration[]{new Declaration( "$cheese",
                                                                       new PatternExtractor(new ClassObjectType(Cheese.class ) ),
                                                                       null ), new Declaration( "$person",
                                                                                                new PatternExtractor( new ClassObjectType( Person.class ) ),
                                                                                                null )};
        final String[] globals = new String[]{"aGlobal", "anotherGlobal"};
        final List globalTypes = Arrays.asList(new String[]{"String", "String"} );

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
                 "Rule0" + StringUtils.ucFirst(className ) + "Invoker" );

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
                 new Attribute[]{new Attribute("int",
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
        map.put( "isMultiPattern",
                 Boolean.TRUE );



        LegacyAccumulate la = new LegacyAccumulate();

        final CompilationUnit build = la.build(map);

        assertEquals(multiPatternResultParsed, build);
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

    public class Cheese
            implements
            Serializable {

        public static final String STILTON = "stilton";

        public static final int BASE_PRICE = 10;

        private static final long serialVersionUID = 510l;
        private String            type;
        private int               price;
        private int               oldPrice;
        private Date usedBy;
        private double            doublePrice;

        public Cheese() {

        }

        public Cheese(final String type) {
            super();
            this.type = type;
            this.price = 0;
        }

        public Cheese(final String type,
                      final int price) {
            super();
            this.type = type;
            this.price = price;
        }

        public Cheese(final String type,
                      final int price,
                      final int oldPrice ) {
            super();
            this.type = type;
            this.price = price;
            this.oldPrice = oldPrice;
        }

        public int getPrice() {
            return this.price;
        }

        public String getType() {
            return this.type;
        }

        public void setType(final String type) {
            this.type = type;
        }

        public void setPrice(final int price) {
            this.price = price;
        }

        public String toString() {
            return "Cheese( type='" + this.type + "', price=" + this.price + " )";
        }

        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + price;
            result = PRIME * result + ((type == null) ? 0 : type.hashCode());
            return result;
        }

        public boolean equals(Object obj) {
            if ( this == obj ) return true;
            if ( obj == null ) return false;
            if ( getClass() != obj.getClass() ) return false;
            final Cheese other = (Cheese) obj;
            if ( price != other.price ) return false;
            if ( type == null ) {
                if ( other.type != null ) return false;
            } else if ( !type.equals( other.type ) ) return false;
            return true;
        }

        public int getOldPrice() {
            return oldPrice;
        }

        public void setOldPrice(int oldPrice) {
            this.oldPrice = oldPrice;
        }

        public Date getUsedBy() {
            return usedBy;
        }

        public void setUsedBy(Date usedBy) {
            this.usedBy = usedBy;
        }

        public synchronized double getDoublePrice() {
            return doublePrice;
        }

        public synchronized void setDoublePrice(double doublePrice) {
            this.doublePrice = doublePrice;
        }



    }


}