package org.drools.mvelcompiler;

import org.junit.Test;

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ModifyCompilerTest implements CompilerTest {

    @Test
    public void testUncompiledMethod() {
        test("{modify( (List)$toEdit.get(0) ){ setEnabled( true ) }}",
             "{ ((java.util.List) $toEdit.get(0)).setEnabled(true); }",
             result -> assertThat(allModifiedProperties(result), is(empty())));
    }
}