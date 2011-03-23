/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.clips;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.CommonTokenStream;
import org.drools.RuleBase;
import org.drools.RuleBaseFactory;
import org.drools.StatefulSession;
import org.drools.base.ClassTypeResolver;
import org.drools.builder.ResourceType;
import org.drools.clips.functions.AssertFunction;
import org.drools.clips.functions.BindFunction;
import org.drools.clips.functions.CallFunction;
import org.drools.clips.functions.CreateListFunction;
import org.drools.clips.functions.EqFunction;
import org.drools.clips.functions.GetFunction;
import org.drools.clips.functions.IfFunction;
import org.drools.clips.functions.LessThanFunction;
import org.drools.clips.functions.LessThanOrEqFunction;
import org.drools.clips.functions.MinusFunction;
import org.drools.clips.functions.ModifyFunction;
import org.drools.clips.functions.MoreThanFunction;
import org.drools.clips.functions.MoreThanOrEqFunction;
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
import org.drools.io.ResourceFactory;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.lang.descr.TypeDeclarationDescr;
import org.drools.rule.ImportDeclaration;
import org.drools.rule.MVELDialectRuntimeData;
import org.drools.rule.Namespaceable;
import org.drools.rule.Package;
import org.drools.runtime.Globals;
import org.drools.runtime.rule.FactHandle;
import org.drools.spi.GlobalResolver;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.UnresolveablePropertyException;
import org.mvel2.compiler.ExpressionCompiler;
import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.MapVariableResolver;
import org.mvel2.integration.impl.MapVariableResolverFactory;

/**
 * An interactive Clips session shell.
 * You can launch this as a Main class, no parameters are required.
 */
public class ClipsShell
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

    //private ClassTypeResolver   typeResolver;

    private String              moduleName;
    private static final String MAIN = "MAIN";

    private ClipsVariableResolverFactory   factory;

    public ClipsShell() {
        this( RuleBaseFactory.newRuleBase() );
        init( this );
    }
    
    public static void init(ClipsShell shell ) {
        FunctionHandlers handlers = FunctionHandlers.getInstance();
        handlers.registerFunction( new PlusFunction() );
        handlers.registerFunction( new MinusFunction() );
        handlers.registerFunction( new MultiplyFunction() );
        handlers.registerFunction( new ModifyFunction() );
        handlers.registerFunction( new CreateListFunction() );
        handlers.registerFunction( new PrintoutFunction() );
        handlers.registerFunction( new PrognFunction() );
        handlers.registerFunction( new IfFunction() );
        handlers.registerFunction( new LessThanFunction() );
        handlers.registerFunction( new LessThanOrEqFunction() );
        handlers.registerFunction( new MoreThanFunction() );
        handlers.registerFunction( new MoreThanOrEqFunction() );
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
    }

    public static void main(String[] args) throws Exception {
        ClipsShell shell = new ClipsShell();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        shell.addRouter( "t", new PrintStream( out ) );
        
        StringBuffer buf = new StringBuffer();
        System.out.print("Drools>");

        StringBuffer sessionLog = new StringBuffer();
        while(true) {
            byte name[] = new byte[256];

            System.in.read(name);
            String cmd = (new String(name)).trim();

            if (cmd.equals("(exit)") || cmd.equals("(quit)")) {
                sessionLog.append(cmd);
                break;
            }
            buf.append(cmd);

            if (isBalancedBrackets(buf)) {
                String exp = buf.toString();
                if (exp.startsWith("(save ")) {
                    String file = getFileName(exp);
                    System.out.println("Saving transcript to [" + file + "]");
                    writeFile(file, sessionLog);
                    sessionLog = new StringBuffer();
                    System.out.print("Drools>");
                } else {
                    sessionLog.append(cmd + "\n");

                    if (exp.startsWith("(load ")) {
                        String file = getFileName(exp);
                        System.out.println("Loading transcript from [" + file + "]");
                        exp = loadFile(file);
                    }

                    shell.eval(exp);
                    String output = new String(out.toByteArray());
                    if (output != null && output.trim().length() > 0) {
                        System.out.println(output);
                    }
                    out.reset();
                    System.out.print("Drools>");
                    buf = new StringBuffer();
                }
            }
        }

        System.out.println("Goodbye, and good luck !");

    }

    private static String loadFile(String fileName) throws IOException {
        File f = new File(fileName);
        InputStream is = new FileInputStream(f);

        long length = f.length();
        byte[] bytes = new byte[(int)length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            throw new IOException("Could not completely read file "+f.getName());
        }

        is.close();
        return new String(bytes);
    }

    private static String getFileName(String exp) {
        char qt = '\'';
        if (exp.contains("\"")) {
            qt = '"';
        }
        String file = exp.substring(exp.indexOf(qt) + 1, exp.lastIndexOf(qt));
        return file;
    }

    private static void writeFile(String file, StringBuffer sessionLog) {
        FileOutputStream fout;
        try {
            File f = new File(file);
            if (!f.exists()) {
                f.createNewFile();
            }
            fout = new FileOutputStream(f);
            fout.write(sessionLog.toString().getBytes());
            fout.flush();
            fout.close();
        } catch (FileNotFoundException e) {
            System.err.println("File " + file + " does not exist.");
        } catch (IOException e) {
            e.printStackTrace();
        }


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

    public ClipsShell(RuleBase ruleBase) {
        this.moduleName = MAIN;
        this.ruleBase = ruleBase;

        this.packageBuilder = new PackageBuilder( this.ruleBase );
        String str = "global java.util.Map printrouters\n";
        packageBuilder.addKnowledgeResource( ResourceFactory.newByteArrayResource( str.getBytes() ), ResourceType.DRL, null );

        this.session = this.ruleBase.newStatefulSession();
        // this.functions = new HashMap();
        //        this.directImports = new HashMap();
        //        this.dynamicImports = new HashSet();

        //        this.typeResolver = new ClassTypeResolver( new HashSet(),
        //                                                   ((InternalRuleBase) this.ruleBase).getConfiguration().getClassLoader() );

        this.vars = new HashMap<String, Object>();
        GlobalResolver2 globalResolver = new GlobalResolver2( this.vars,
                                                              this.session.getGlobalResolver() );
        this.session.setGlobalResolver( globalResolver );        
        
        
        
        this.factory =  new ClipsVariableResolverFactory( ruleBase,
                                                          new HashMap<String, Object> (),
                                                          globalResolver, 
                                                          null );
        
    }

    public StatefulSession getStatefulSession() {
        return this.session;
    }

    public static class GlobalResolver2
        implements
        GlobalResolver {
        private Map<String, Object> vars;
        private GlobalResolver      delegate;

        public GlobalResolver2() {
        }

        public GlobalResolver2(Map<String, Object> vars,
                               GlobalResolver delegate) {
            this.vars = vars;
            this.delegate = delegate;
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            vars = (Map<String, Object>) in.readObject();
            delegate = (GlobalResolver) in.readObject();
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( vars );
            out.writeObject( delegate );
        }

        public Object resolveGlobal(String identifier) {
            Object object = this.vars.get( identifier );
            if ( object == null ) {
                object = delegate.resolveGlobal( identifier );
            }
            return object;
        }

        public void setGlobal(String identifier,
                              Object value) {
            this.delegate.setGlobal( identifier,
                                     value );
        }
    }

    public void importHandler(ImportDescr descr) {
        // use the current focus as the default namespace for these imports
        PackageDescr pkgDescr = createPackageDescr( this.session.getAgenda().getFocusName() );
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
        // TODO: fix this
//        List list = (List) functionDescr.getBody();
//        for ( Iterator it = list.iterator(); it.hasNext(); ) {
//            FunctionHandlers.dump( (LispForm) it.next(),
//                                   builder, 
//                                   true );
//        }
        builder.append( "}" );

        System.out.println( "mvel expr:" + builder.toString() );
        
        functionDescr.setText( builder.toString() );
        functionDescr.setDialect( "clips" );

        PackageDescr pkgDescr = createPackageDescr( functionDescr.getNamespace() );
        pkgDescr.addFunction( functionDescr );

        this.packageBuilder.addPackage( pkgDescr );
    }

    public void lispFormHandler(LispForm lispForm) {
        StringBuilderAppendable appendable = new StringBuilderAppendable();
        FunctionHandlers.dump( lispForm,
                               appendable,
                               true );
        
        ClassLoader tempClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader( ((InternalRuleBase)ruleBase).getRootClassLoader() );

            ParserContext context = new ParserContext();
            
    
            String namespace = this.session.getAgenda().getFocusName();
    
            Package pkg = this.ruleBase.getPackage( namespace );
            if ( pkg == null ) {
                this.packageBuilder.addPackage( createPackageDescr( namespace ) );
                pkg = this.ruleBase.getPackage( namespace );
                
            }
    
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

                            Class cls = ((InternalRuleBase)ruleBase).getRootClassLoader().loadClass( importName );
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
        } finally {
            Thread.currentThread().setContextClassLoader( tempClassLoader );
        }
    }

    public void templateHandler(TypeDeclarationDescr typeDescr) {
        setModuleName( typeDescr );

        PackageDescr pkg = createPackageDescr( typeDescr.getNamespace() );
        
        pkg.addTypeDeclaration( typeDescr );

        this.packageBuilder.addPackage( pkg );
    }

    public void ruleHandler(RuleDescr ruleDescr) {
        setModuleName( ruleDescr );
        PackageDescr pkg = createPackageDescr( ruleDescr.getNamespace() );
        pkg.addRule( ruleDescr );

        this.packageBuilder.addPackage( pkg );

        this.session.fireAllRules();    }

    public void setModuleName(Namespaceable namespaceable) {
        // if the namespace is not set, set it to the current focus module
        if ( isEmpty( namespaceable.getNamespace() ) ) {
            namespaceable.setNamespace( this.session.getAgenda().getFocusName() );
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
            throw new RuntimeException( e );
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
        this.factory.createVariable( function.getName(),
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
    
    public Map getVariables() {
        return this.vars;
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

    public void addVariable(String name,
                            Object value) {
        if ( name.startsWith( "?" ) ) {
            name = name.substring( 1 );
        }
        this.factory.createVariable( name,
                                     value );
    }


    private PackageDescr createPackageDescr(String moduleName) {
        PackageDescr pkg = new PackageDescr( moduleName );
        pkg.addAttribute( new AttributeDescr( "dialect",
                                              "clips" ) );
        return pkg;
    }
    
    public static class ClipsVariableResolverFactory extends MapVariableResolverFactory {
        private GlobalResolver globals;
        private InternalRuleBase ruleBase;
        
        public ClipsVariableResolverFactory(RuleBase ruleBase, Map<String, Object> variables, GlobalResolver globals, VariableResolverFactory nextFactory) {
            super( variables, nextFactory );
            this.ruleBase = (InternalRuleBase)ruleBase;
            this.globals = globals;
        }
        
        public VariableResolver createVariable(String name, Object value) {
            VariableResolver vr;

            try {
                (vr = getVariableResolver(name)).setValue(value);
                return vr;
            }
            catch (UnresolveablePropertyException e) {
                addResolver(name, vr = new MapVariableResolver(variables, name)).setValue(value);
                return vr;
            }
        }

        public VariableResolver createVariable(String name, Object value, Class<?> type) {
            VariableResolver vr;
            try {
                vr = getVariableResolver(name);
            }
            catch (UnresolveablePropertyException e) {
                vr = null;
            }

            if (vr != null && vr.getType() != null) {
                throw new RuntimeException("variable already defined within scope: " + vr.getType() + " " + name);
            }
            else {
                addResolver(name, vr = new MapVariableResolver(variables, name, type)).setValue(value);
                return vr;
            }
        }

        public VariableResolver getVariableResolver(String name) {
            VariableResolver vr = variableResolvers.get(name);
            if (vr != null) {
                return vr;
            }
            else if (variables.containsKey(name)) {
                variableResolvers.put(name, vr = new MapVariableResolver(variables, name));
                return vr;
            } else if ( this.ruleBase.getGlobals().containsKey( name ) ) {
                variableResolvers.put(name, vr = new GlobalsVariableResolver(name, (Class) this.ruleBase.getGlobals().get( name ), globals ) );
                return vr;                
            }
            else if (nextFactory != null) {
                return nextFactory.getVariableResolver(name);
            }

            throw new UnresolveablePropertyException("unable to resolve variable '" + name + "'");
        }


        public boolean isResolveable(String name) {
            return (variableResolvers.containsKey(name))
                    || (variables != null && variables.containsKey(name))
                    ||  this.ruleBase.getGlobals().containsKey( name )
                    || (nextFactory != null && nextFactory.isResolveable(name));
        }

        protected VariableResolver addResolver(String name, VariableResolver vr) {
            variableResolvers.put(name, vr);
            return vr;
        }


        public boolean isTarget(String name) {
            return variableResolvers.containsKey(name);
        }

        public Set<String> getKnownVariables() {
            Set<String> vars = super.getKnownVariables();
            vars.addAll( this.ruleBase.getGlobals().keySet() );
            return vars;
        }   
    }
    
    public static class GlobalsVariableResolver implements VariableResolver {
        private String name;
        private Class<?> knownType;
        private GlobalResolver globals;
        
        public GlobalsVariableResolver(String name,
                                       Class knownType,
                                       GlobalResolver globals) {
            this.name = name;
            this.knownType = knownType;
            this.globals = globals;
        }

        public String getName() {
            return this.name;
        }

        public Class getType() {
            return knownType;
        }

        public void setStaticType(Class type) {
            this.knownType = type;  
        }

        public int getFlags() {
            return 0;
        }

        public Object getValue() {
            return this.globals.resolveGlobal( name );
        }

        public void setValue(Object value) {
            this.globals.setGlobal( name, value );
        }
        
    }

}
