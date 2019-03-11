package org.drools;

import org.drools.mvelcompiler.MvelCompiler;
import org.drools.mvelcompiler.ParsingResult;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.junit.Test;

import static org.junit.Assert.*;

public class MvelCompilerTest {

    @Test
    public void testConvertPropertyToAccessor() {
        MvelCompilerContext mvelCompilerContext = new MvelCompilerContext().addDeclaration("$p", Person.class);
        ParsingResult compiled = new MvelCompiler(mvelCompilerContext).compile("{ $p.parent.name; } ");
        assertEquals("$p.getParent().getName()", compiled.resultAsString());
    }

    @Test
    public void testStringLength() {
        MvelCompilerContext mvelCompilerContext = new MvelCompilerContext().addDeclaration("$p", Person.class);
        ParsingResult compiled = new MvelCompiler(mvelCompilerContext).compile("{ $p.name.length; }");
        assertEquals("$p.getName().length()", compiled.resultAsString());
    }

    @Test
    public void testAssignment() {
        MvelCompilerContext mvelCompilerContext = new MvelCompilerContext().addDeclaration("$p", Person.class);;
        ParsingResult compiled = new MvelCompiler(mvelCompilerContext).compile("{ Person np = $p; }");
        assertEquals("org.drools.Person np = $p", compiled.resultAsString());
    }
}