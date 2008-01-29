package org.drools.clp.mvel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.TestCase;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CommonTokenStream;
import org.drools.clp.CLPMVELLexer;
import org.drools.clp.CLPMVELParser;
import org.drools.clp.ParserHandler;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.RuleDescr;
import org.mvel.MVEL;
import org.mvel.ast.Function;
import org.mvel.compiler.CompiledExpression;
import org.mvel.compiler.ExpressionCompiler;
import org.mvel.util.CompilerTools;

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

    public static class Shell
        implements
        ParserHandler,
        VariableContext,
        FunctionContext,
        PrintRouterContext,
        MVELBuildContext {
        private Map<String, Object> vars;
        private Map<String, String> varNameMap;

        public Shell() {
            this.vars = new HashMap<String, Object>();
            this.varNameMap = new HashMap<String, String>();
        }

        public void functionHandler(FunctionDescr ruleDescr) {
            Appendable builder = new StringBuilderAppendable();

            // strip lead/trailing quotes
            String name = ruleDescr.getName().trim();
            if ( name.charAt( 0 ) == '"' ) {
                name = name.substring( 1 );
            }

            if ( name.charAt( name.length() - 1 ) == '"' ) {
                name = name.substring( 0,
                                       name.length() - 1 );
            }
            builder.append( "function " + name + "(" );

            for ( int i = 0, length = ruleDescr.getParameterNames().size(); i < length; i++ ) {
                builder.append( ruleDescr.getParameterNames().get( i ) );
                if ( i < length - 1 ) {
                    builder.append( ", " );
                }
            }

            builder.append( ") {\n" );
            List list = (List) ruleDescr.getContent();
            for ( Iterator it = list.iterator(); it.hasNext(); ) {
                FunctionHandlers.dump( (LispForm) it.next(),
                                       builder,
                                       this );
            }
            builder.append( "}" );

            ExpressionCompiler compiler = new ExpressionCompiler( builder.toString() );
            Serializable s1 = compiler.compile();
            Map<String, org.mvel.ast.Function> map = CompilerTools.extractAllDeclaredFunctions( (CompiledExpression) s1 );
            for ( org.mvel.ast.Function function : map.values() ) {
                addFunction( function );
            }

        }

        public void importHandler(ImportDescr descr) {
            // TODO Auto-generated method stub

        }

        public void lispFormHandler(LispForm lispForm) {
            StringBuilderAppendable appendable = new StringBuilderAppendable();
            FunctionHandlers.dump( lispForm,
                                   appendable,
                                   this );
            MVEL.eval( appendable.toString(),
                       this.vars );

        }

        public void ruleHandler(RuleDescr ruleDescr) {
            // TODO Auto-generated method stub

        }

        public void eval(String string) {
            eval( new StringReader( string ) );
        }

        public void eval(Reader reader) {
            CLPMVELParser parser;
            try {
                parser = new CLPMVELParser( new CommonTokenStream( new CLPMVELLexer( new ANTLRReaderStream( reader ) ) ) );
                parser.eval( this,
                             this );
            } catch ( Exception e ) {
                e.printStackTrace();
            }
        }

        public void addFunction(Function function) {
            this.vars.put( function.getAbsoluteName(),
                           function );
        }

        public boolean removeFunction(String functionName) {
            return (this.vars.remove( functionName ) != null);
        }

        public Map<String, Function> getFunctions() {
            Map<String, Function> map = new HashMap<String, Function>();
            for ( Iterator it = this.vars.entrySet().iterator(); it.hasNext(); ) {
                Entry entry = (Entry) it.next();
                if ( entry.getValue() instanceof Function ) {
                    map.put( (String) entry.getKey(),
                             (Function) entry.getValue() );
                }
            }
            return map;
        }

        public void addRouter(String name,
                              PrintStream out) {

            Map routers = (Map) this.vars.get( "printrouters" );
            if ( routers == null ) {
                routers = new HashMap();
                this.vars.put( "printrouters",
                               routers );
            }
            routers.put( name,
                         out );

        }

        public boolean removeRouter(String name) {
            return (this.vars.remove( name ) != null);
        }

        public Map<String, PrintStream> getRouters() {
            Map<String, PrintStream> map = new HashMap<String, PrintStream>();
            for ( Iterator it = this.vars.entrySet().iterator(); it.hasNext(); ) {
                Entry entry = (Entry) it.next();
                if ( entry.getValue() instanceof Function ) {
                    map.put( (String) entry.getKey(),
                             (PrintStream) entry.getValue() );
                }
            }
            return map;
        }

        public void addVariable(String name,
                                Object value) {
            String temp = this.varNameMap.get( name );
            if ( temp == null ) {
                temp = makeValid( name );
                if ( !temp.equals( name ) ) {
                    this.varNameMap.put( name,
                                         temp );
                }
            }
            this.vars.put( temp,
                           value );
        }

        public void removeVariable(String name) {
            String temp = this.varNameMap.get( name );
            if ( temp != null ) {
                name = temp;
            }
            this.vars.remove( name );
        }

        public Map<String, String> getVariableNameMap() {
            return this.varNameMap;
        }

        public String makeValid(String var) {
            var = var.replaceAll( "\\?",
                                  "_Q_" );
            return var;
        }

    }

}
