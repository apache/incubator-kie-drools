/**
 *
 */
package org.drools.clips;

import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.Map.Entry;
import java.util.logging.ConsoleHandler;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CommonTokenStream;
import org.drools.FactHandle;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.base.ClassTypeResolver;
import org.drools.base.mvel.DroolsMVELFactory;
import org.drools.clips.functions.AssertFunction;
import org.drools.clips.functions.BindFunction;
import org.drools.clips.functions.CallFunction;
import org.drools.clips.functions.CreateListFunction;
import org.drools.clips.functions.EqFunction;
import org.drools.clips.functions.GetFunction;
import org.drools.clips.functions.IfFunction;
import org.drools.clips.functions.LessThanFunction;
import org.drools.clips.functions.ModifyFunction;
import org.drools.clips.functions.MoreThanFunction;
import org.drools.clips.functions.MultiplyFunction;
import org.drools.clips.functions.NewFunction;
import org.drools.clips.functions.PlusFunction;
import org.drools.clips.functions.PrintoutFunction;
import org.drools.clips.functions.PrognFunction;
import org.drools.clips.functions.ReturnFunction;
import org.drools.clips.functions.RunFunction;
import org.drools.clips.functions.SetFunction;
import org.drools.clips.functions.SwitchFunction;
import org.drools.common.InternalRuleBase;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.PatternDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.lang.descr.TypeDeclarationDescr;
import org.drools.lang.descr.TypeFieldDescr;
import org.drools.rule.ImportDeclaration;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Namespaceable;
import org.drools.rule.Package;
import org.drools.rule.builder.dialect.mvel.MVELDialectConfiguration;
import org.drools.spi.GlobalResolver;
import org.mvel.MVEL;
import org.mvel.ParserContext;
import org.mvel.ast.Function;
import org.mvel.compiler.CompiledExpression;
import org.mvel.compiler.ExpressionCompiler;
import org.mvel.debug.DebugTools;
import org.mvel.util.CompilerTools;

public class Shell
    implements
    ParserHandler,
    VariableContext,
    FunctionContext,
    PrintRouterContext {
    private Map<String, Object> vars;

    private PackageBuilder      packageBuilder;
    private RuleBase            ruleBase;
    private StatefulSession     session;

    // private Map                 functions;

    //    private Map                 directImports;
    //    private Set                 dynamicImports;

    private ClassTypeResolver   typeResolver;

    private String              moduleName;
    private static final String MAIN = "MAIN";

    private DroolsMVELFactory   factory;

    public Shell() {
        this( RuleBaseFactory.newRuleBase() );
    }

    public static void main(String[] args) throws Exception {


        StringBuffer buf = new StringBuffer();
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
        handlers.registerFunction( new RunFunction() );
        handlers.registerFunction( new BindFunction() );
        handlers.registerFunction( new NewFunction() );
        handlers.registerFunction( new SetFunction() );
        handlers.registerFunction( new GetFunction() );
        handlers.registerFunction( new CallFunction() );
        handlers.registerFunction( new AssertFunction() );
        Shell shell = new Shell();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        shell.addRouter( "t",
                new PrintStream( out ) );
        while(true) {
            byte name[] = new byte[256];
	        System.out.print("Drools>");
	        Thread.sleep(1);
	        System.in.read(name);
	        String cmd = (new String(name)).trim();
	        //System.out.println("ECHO:" + cmd);
	        if (cmd.equals("(exit)") || cmd.equals("(quit)")) break;
	        buf.append(cmd);

	        if (isBalancedBrackets(buf)) {
	        	shell.eval(buf.toString());
	        	String output = new String(out.toByteArray());
	        	if (output != null && output.trim().length() > 0) {
	        		System.out.println(output);
	        	} else {
	        		System.out.println("OK");
	        	}
	        	out.reset();
	        	buf = new StringBuffer();
	        }
        }

        System.out.println("Goobye !");

    }

    private static boolean isBalancedBrackets(StringBuffer buf) {
		char[] cs = buf.toString().toCharArray();
		int stack = 0;
		for (int i = 0; i < cs.length; i++) {
			if (cs[i] == '(') stack++;
			if (cs[i] == ')') stack--;
		}
		return stack == 0;
	}

	public Shell(RuleBase ruleBase) {
        this.moduleName = MAIN;
        this.ruleBase = ruleBase;

        this.packageBuilder = new PackageBuilder( this.ruleBase );

        this.session = this.ruleBase.newStatefulSession();
        // this.functions = new HashMap();
        //        this.directImports = new HashMap();
        //        this.dynamicImports = new HashSet();

        //        this.typeResolver = new ClassTypeResolver( new HashSet(),
        //                                                   ((InternalRuleBase) this.ruleBase).getConfiguration().getClassLoader() );

        this.factory = (DroolsMVELFactory) new DroolsMVELFactory( null,
                                                                  null,
                                                                  ((InternalRuleBase) this.ruleBase).getGlobals() );

        this.vars = new HashMap<String, Object>();
        GlobalResolver2 globalResolver = new GlobalResolver2( this.vars,
                                                              this.session.getGlobalResolver() );
        this.session.setGlobalResolver( globalResolver );

        this.factory.setContext( null,
                                 null,
                                 null,
                                 this.session,
                                 this.vars );

        addRouter( "t",
                   System.out );
    }

    public StatefulSession getStatefulSession() {
        return this.session;
    }

    public static class GlobalResolver2
        implements
        GlobalResolver {
        private Map<String, Object> vars;
        private GlobalResolver      resolver;

        public GlobalResolver2() {
        }

        public GlobalResolver2(Map<String, Object> vars,
                               GlobalResolver resolver) {
            this.vars = vars;
            this.resolver = resolver;
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            vars = (Map<String, Object>) in.readObject();
            resolver = (GlobalResolver) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( vars );
            out.writeObject( resolver );
        }

        public Object resolveGlobal(String identifier) {
            Object object = this.vars.get( identifier );
            if ( object == null ) {
                object = resolver.resolveGlobal( identifier );
            }
            return object;
        }

        public void setGlobal(String identifier,
                              Object value) {
            this.resolver.setGlobal( identifier,
                                     value );

        }
    }

    public void importHandler(ImportDescr descr) {
        // use the current focus as the default namespace for these imports
        PackageDescr pkgDescr = createPackageDescr( this.session.getAgenda().getFocus().getName() );
        pkgDescr.addImport( descr );
        this.packageBuilder.addPackage( pkgDescr );
    }

    public void functionHandler(FunctionDescr functionDescr) {
        // for now all functions are in MAIN
        //setModuleName( functionDescr );
        functionDescr.setNamespace( "MAIN" );
        Appendable builder = new StringBuilderAppendable();

        // strip lead/trailing quotes
        String name = functionDescr.getName().trim();
        if ( name.charAt( 0 ) == '"' ) {
            name = name.substring( 1 );
        }

        if ( name.charAt( name.length() - 1 ) == '"' ) {
            name = name.substring( 0,
                                   name.length() - 1 );
        }
        builder.append( "function " + name + "(" );

        for ( int i = 0, length = functionDescr.getParameterNames().size(); i < length; i++ ) {
            builder.append( functionDescr.getParameterNames().get( i ) );
            if ( i < length - 1 ) {
                builder.append( ", " );
            }
        }

        builder.append( ") {\n" );
        List list = (List) functionDescr.getContent();
        for ( Iterator it = list.iterator(); it.hasNext(); ) {
            FunctionHandlers.dump( (LispForm) it.next(),
                                   builder );
        }
        builder.append( "}" );

        functionDescr.setContent( builder.toString() );
        functionDescr.setDialect( "clips" );

        PackageDescr pkgDescr = createPackageDescr( functionDescr.getNamespace() );
        pkgDescr.addFunction( functionDescr );

        this.packageBuilder.addPackage( pkgDescr );
    }

    public void lispFormHandler(LispForm lispForm) {
        StringBuilderAppendable appendable = new StringBuilderAppendable();
        FunctionHandlers.dump( lispForm,
                               appendable );

        ParserContext context = new ParserContext();

        String namespace = this.session.getAgenda().getFocus().getName();

        Package pkg = this.ruleBase.getPackage( namespace );

        if ( pkg != null ) {
            // only time this will be null is if we have yet to do any packagedescr work

            try {
                for ( Iterator it = pkg.getImports().entrySet().iterator(); it.hasNext(); ) {
                    Entry entry = (Entry) it.next();
                    String importName = ((ImportDeclaration) entry.getValue()).getTarget();
                    if ( importName.endsWith( "*" )) {
                        context.addPackageImport( importName.substring( 0,
                                                                        importName.length() - 2 ) );
                    } else {
                        Class cls = pkg.getDialectRuntimeRegistry().getClassLoader().loadClass( importName );
                        context.addImport( cls.getSimpleName(),
                                           (Class) cls );
                    }
                }
            } catch ( Exception e ) {
                e.printStackTrace();
            }

            MVELDialectRuntimeData data = (MVELDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData( "clips" );
            this.factory.setNextFactory( data.getFunctionFactory() );
        }

        ExpressionCompiler expr = new ExpressionCompiler( appendable.toString() );
        Serializable executable = expr.compile( context );

        MVEL.executeExpression( executable,
                                this,
                                this.factory );
    }

    public void templateHandler(TypeDeclarationDescr typeDescr) {
        setModuleName( typeDescr );

        PackageDescr pkg = createPackageDescr( typeDescr.getNamespace() );
        //pkg.addRule( ruleDescr );
        pkg.addTypeDeclaration( typeDescr );

        this.packageBuilder.addPackage( pkg );

        //        try {
        //            this.ruleBase.addPackage( builder.getPackage() );
        //        } catch ( Exception e ) {
        //            e.printStackTrace();
        //        }
    }

    public void ruleHandler(RuleDescr ruleDescr) {
        setModuleName( ruleDescr );
        PackageDescr pkg = createPackageDescr( ruleDescr.getNamespace() );
        pkg.addRule( ruleDescr );

        this.packageBuilder.addPackage( pkg );

        this.session.fireAllRules();

        //        try {
        //            this.ruleBase.addPackage( builder.getPackage() );
        //        } catch ( Exception e ) {
        //            e.printStackTrace();
        //        }
    }

    public void setModuleName(Namespaceable namespaceable) {
        // if the namespace is not set, set it to the current focus module
        if ( isEmpty( namespaceable.getNamespace() ) ) {
            namespaceable.setNamespace( this.session.getAgenda().getFocus().getName() );
        }
    }

    public boolean isEmpty(String string) {
        return (string == null || string.trim().length() == 0);
    }

    public void eval(String string) {
        eval( new StringReader( string ) );
    }

    public void eval(Reader reader) {
        ClipsParser parser;
        try {
            parser = new ClipsParser( new CommonTokenStream( new ClipsLexer( new ANTLRReaderStream( reader ) ) ) );
            parser.eval( this );
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    public void run() {
        this.session.fireAllRules();
    }

    public void run(int fireLimit) {
        this.session.fireAllRules( fireLimit );
    }

    public FactHandle insert(Object object) {
        return this.session.insert( object );
    }

    public void importEntry(String importEntry) {

    }

    public void addFunction(Function function) {
        this.factory.createVariable( function.getAbsoluteName(),
                                     function );
    }

    public boolean removeFunction(String functionName) {
        return false; //(this.vars.remove( functionName ) != null);
    }

    public Map<String, Function> getFunctions() {
        Map<String, Function> map = new HashMap<String, Function>();
        //        for ( Iterator it = this.vars.entrySet().iterator(); it.hasNext(); ) {
        //            Entry entry = (Entry) it.next();
        //            if ( entry.getValue() instanceof Function ) {
        //                map.put( (String) entry.getKey(),
        //                         (Function) entry.getValue() );
        //            }
        //        }
        return map;
    }

    public void addRouter(String name,
                          PrintStream out) {

        Map routers = (Map) this.vars.get( "printrouters" );
        if ( routers == null ) {
            routers = new HashMap();
            this.factory.createVariable( "printrouters",
                                         routers );
        }

        routers.put( name,
                     out );

    }

    public boolean removeRouter(String name) {
        return false; //(this.vars.remove( name ) != null);
    }

    //    public Map<String, PrintStream> getRouters() {
    //        Map<String, PrintStream> map = new HashMap<String, PrintStream>();
    //        for ( Iterator it = this.vars.entrySet().iterator(); it.hasNext(); ) {
    //            Entry entry = (Entry) it.next();
    //            if ( entry.getValue() instanceof Function ) {
    //                map.put( (String) entry.getKey(),
    //                         (PrintStream) entry.getValue() );
    //            }
    //        }
    //        return map;
    //    }

    public void addVariable(String name,
                            Object value) {
        if ( name.startsWith( "?" ) ) {
            name = name.substring( 1 );
        }
        this.factory.createVariable( name,
                                     value );
        //        this.session.setGlobal( name,
        //                                value );
    }

    //    public void removeVariable(String name) {
    //        String temp = this.varNameMap.get( name );
    //        if ( temp != null ) {
    //            name = temp;
    //        }
    //        this.session.getGlobal( identifier ).remove( name );
    //    }

    private PackageDescr createPackageDescr(String moduleName) {
        PackageDescr pkg = new PackageDescr( moduleName );
        pkg.addAttribute( new AttributeDescr( "dialect",
                                              "clips" ) );

        //        for ( Iterator it = this.typeResolver.getImports().iterator(); it.hasNext(); ) {
        //            pkg.addImport( new ImportDescr( (String) it.next() ) );
        //        }

        return pkg;
    }

}