package org.drools.examples.wumpus;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mvel2.MVEL;
import org.mvel2.ParserConfiguration;
import org.mvel2.ParserContext;
import org.mvel2.compiler.ExecutableStatement;
import org.mvel2.tests.core.res.Cheese;
import org.mvel2.tests.core.res.Column;

public class MVELTest {

    @Test
    public void testOperatorWithoutSpace() {
        String str = "length == ($c.length -1)"; // only works with a space after the minus operator

        ParserConfiguration pconf = new ParserConfiguration();
        ParserContext pctx = new ParserContext(pconf);
        ExecutableStatement stmt = (ExecutableStatement) MVEL.compileExpression(str, pctx);
        Column col1 = new Column("x",0);
        Column col2 = new Column("x", 0);
        Map<String, Object> vars = new HashMap<String, Object> ();
        vars.put(  "$c", col2 );
        Boolean result = (Boolean) MVEL.executeExpression(stmt, col1, vars);
        assertFalse(result);
      } 

}
