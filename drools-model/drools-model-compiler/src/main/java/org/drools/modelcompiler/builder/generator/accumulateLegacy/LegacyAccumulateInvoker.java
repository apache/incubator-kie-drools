package org.drools.modelcompiler.builder.generator.accumulateLegacy;

import java.lang.reflect.Method;
import java.util.Map;

import org.drools.core.rule.Declaration;
import org.drools.javaparser.JavaParser;
import org.drools.javaparser.ast.CompilationUnit;
import org.drools.javaparser.ast.body.ClassOrInterfaceDeclaration;
import org.drools.javaparser.ast.body.MethodDeclaration;

public class LegacyAccumulateInvoker {

    public final Map<String, Object> map;

    public String fixedPart = "package __mypackage;\n" +
            "\n" +
            "import org.mvel2.asm.ClassReader;\n" +
            "import org.mvel2.asm.util.TraceMethodVisitor;\n" +
            "import org.drools.core.util.asm.MethodComparator.Tracer;\n" +
            "import java.util.Collections;\n" +
            "public class _CLASS_NAME implements org.drools.core.spi.Accumulator, org.drools.core.spi.CompiledInvoker\n" +
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

    public LegacyAccumulateInvoker(Map<String, Object> map) {
        this.map = map;
    }

    public CompilationUnit build() {

        final CompilationUnit cu = JavaParser.parse(fixedPart);

        setPackage(cu);
        setInvokerClassName(cu);


        MethodDeclaration initMethod = cu.findAll(MethodDeclaration.class, m -> m.getName().asString().equals("init")).iterator().next();

        for(Declaration d : (Declaration[]) map.get("declarations")) {
             String res = "@{declr.typeName} @{declr.identifier} = ( @{declr.boxedTypeName} ) previousDeclarations[@{i0}].@{declr.nativeReadMethod.name}( (org.drools.core.common.InternalWorkingMemory) workingMemory, tuple.getObject( previousDeclarations[@{i0}] ) );";


        }

        return cu;
    }

    private CompilationUnit setPackage(CompilationUnit cu) {
        return cu.setPackageDeclaration((String) map.get("package"));
    }

    private void setInvokerClassName(CompilationUnit cu) {
        final String className = (String) map.get("invokerClassName");
        cu.findAll(ClassOrInterfaceDeclaration.class)
                .stream()
                .forEach(n -> n.setName(className));
    }
}
