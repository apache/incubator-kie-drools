package org.drools;

import org.drools.mvelcompiler.MvelCompiler;
import org.drools.mvelcompiler.ParsingResult;
import org.drools.mvelcompiler.context.MvelCompilerContext;
import org.junit.Test;

import static org.hamcrest.Matchers.equalToIgnoringWhiteSpace;
import static org.junit.Assert.*;

public class MvelCompilerTest {

    @Test
    public void testConvertPropertyToAccessor() {
        MvelCompilerContext mvelCompilerContext = new MvelCompilerContext().addDeclaration("$p", Person.class);
        ParsingResult compiled = new MvelCompiler(mvelCompilerContext).compile("{ $p.parent.name; } ");
        assertThat(compiled.resultAsString(), equalToIgnoringWhiteSpace("{ $p.getParent().getName(); }"));

    }

    @Test
    public void testStringLength() {
        MvelCompilerContext mvelCompilerContext = new MvelCompilerContext().addDeclaration("$p", Person.class);
        ParsingResult compiled = new MvelCompiler(mvelCompilerContext).compile("{ $p.name.length; }");
        assertThat(compiled.resultAsString(), equalToIgnoringWhiteSpace("{ $p.getName().length(); }"));

    }

    @Test
    public void testAssignment() {
        MvelCompilerContext mvelCompilerContext = new MvelCompilerContext().addDeclaration("$p", Person.class);;
        ParsingResult compiled = new MvelCompiler(mvelCompilerContext).compile("{ Person np = $p; }");
        assertThat(compiled.resultAsString(), equalToIgnoringWhiteSpace("{ org.drools.Person np = $p; }"));
    }

    @Test
    public void testAssignment2() {
        MvelCompilerContext mvelCompilerContext = new MvelCompilerContext().addDeclaration("$p", Person.class);
        ParsingResult compiled = new MvelCompiler(mvelCompilerContext).compile("{ Person np = $p; np = $p; }");

        assertThat(compiled.resultAsString(), equalToIgnoringWhiteSpace("{ org.drools.Person np = $p; np = $p; }"));
    }
}