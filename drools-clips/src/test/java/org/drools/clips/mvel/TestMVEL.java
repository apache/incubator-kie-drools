package org.drools.clips.mvel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import junit.framework.TestCase;

import org.drools.clips.CLPMVELLexer;
import org.drools.clips.CLPMVELParser;
import org.drools.clips.CreateListFunction;
import org.drools.clips.EqFunction;
import org.drools.clips.FunctionHandlers;
import org.drools.clips.Shell;
import org.drools.clips.functions.IfFunction;
import org.drools.clips.functions.LessThanFunction;
import org.drools.clips.functions.ModifyFunction;
import org.drools.clips.functions.MoreThanFunction;
import org.drools.clips.functions.MultiplyFunction;
import org.drools.clips.functions.PlusFunction;
import org.drools.clips.functions.PrintoutFunction;
import org.drools.clips.functions.PrognFunction;
import org.drools.clips.functions.ReturnFunction;
import org.drools.clips.functions.SwitchFunction;

public class TestMVEL extends TestCase {
    private ByteArrayOutputStream baos;

    Shell                         shell = new Shell();

    public void setUp() {
        FunctionHandlers handlers = FunctionHandlers.getInstance();
        handlers.registerFunction( new PlusFunction() );
        handlers.registerFunction( new MultiplyFunction() );
        handlers.registerFunction( new ModifyFunction() );
        handlers.registerFunction( new CreateListFunction() );
        handlers.registerFunction( new PrintoutFunction() );
        handlers.registerFunction( new PrognFunction() );
        handlers.registerFunction( new IfFunction() );
        handlers.registerFunction( new LessThanFunction() );
        handlers.registerFunction( new MoreThanFunction() );
        handlers.registerFunction( new EqFunction() );
        handlers.registerFunction( new SwitchFunction() );
        //handlers.registerFunction( new DeffunctionFunction() );    
        handlers.registerFunction( new ReturnFunction() );

        this.baos = new ByteArrayOutputStream();
        shell.addRouter( "t",
                         new PrintStream( baos ) );
    }

    //    public void test1() {
    //        String expr = "(* (+ 4 4 ) 2) (create$ 10 20 (+ 10 10) a) (modify ?p (name mark) (location \"london\")(age (+ 16 16) ) ) (printout t a b c (+ 4 4) )";
    //        
    //        SExpression[] lisplists = evalString( expr );
    //
    //        StringBuilderAppendable appendable = new StringBuilderAppendable();        
    //        MVELClipsContext context = new MVELClipsContext();         
    //        for ( SExpression sExpression : lisplists ) {
    //            FunctionHandlers.dump( sExpression, appendable, context );
    //        }
    //        
    //        System.out.println( appendable );
    //    }

    public void testProgn() {
        String expr = "(progn (?x (create$ 10 20 30) ) (printout t ?x) ) )";

        this.shell.eval( expr );

        assertEquals( "102030",
                      new String( baos.toByteArray() ) );
    }

    public void testIf() {
        String expr = "(if (< 1 3) then (printout t hello) (printout t hello) )";

        this.shell.eval( expr );

        assertEquals( "hellohello",
                      new String( baos.toByteArray() ) );
    }

    public void testIfElse() {
        String expr = "(if (eq 1 3) then (printout t hello)  (printout t 1) else (printout t hello)  (printout t 2))";

        this.shell.eval( expr );

        assertEquals( "hello2",
                      new String( baos.toByteArray() ) );
    }

    public void testSwitch() throws IOException {
        String expr = "(switch (?x) (case a then (printout t hello)(printout t 1)) (case b then (printout t hello)(printout t 2)) (default (printout t hello)(printout t 3)) )";

        // check case a
        this.shell.addVariable( "?x",
                                "a" );
        this.shell.eval( expr );
        assertEquals( "hello1",
                      new String( baos.toByteArray() ) );

        // check default
        this.shell.addVariable( "?x",
                                "M" );
        this.shell.eval( expr );
        assertEquals( "hello1hello3",
                      new String( baos.toByteArray() ) );

        // check case b
        this.shell.addVariable( "?x",
                                "b" );
        this.shell.eval( expr );
        assertEquals( "hello1hello3hello2",
                      new String( baos.toByteArray() ) );
    }

    public void testDeffunction() {
        String function = "(deffunction max (?a ?b) (if (> ?a ?b) then (return ?a) else (return ?b) ) )";
        this.shell.eval( function );

        String expr = "(if (eq (max 3 5) 5) then (printout t hello) )";
        this.shell.eval( expr );
        assertEquals( "hello",
                      new String( baos.toByteArray() ) );

        expr = "(if (eq (max ?a ?b) 5) then (printout t hello) )";
        this.shell.addVariable( "?a",
                                "3" );
        this.shell.addVariable( "?b",
                                "5" );
        this.shell.eval( expr );
        assertEquals( "hellohello",
                      new String( baos.toByteArray() ) );
    }

}
