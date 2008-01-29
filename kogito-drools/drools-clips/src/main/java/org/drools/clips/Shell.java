/**
 * 
 */
package org.drools.clips;

import java.io.PrintStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CommonTokenStream;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.RuleDescr;
import org.mvel.MVEL;
import org.mvel.ast.Function;
import org.mvel.compiler.CompiledExpression;
import org.mvel.compiler.ExpressionCompiler;
import org.mvel.util.CompilerTools;

public class Shell
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
        ClipsParser parser;
        try {
            parser = new ClipsParser( new CommonTokenStream( new ClipsLexer( new ANTLRReaderStream( reader ) ) ) );
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