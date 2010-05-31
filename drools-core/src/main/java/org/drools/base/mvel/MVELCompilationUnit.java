package org.drools.base.mvel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.drools.RuntimeDroolsException;
import org.drools.base.ModifyInterceptor;
import org.drools.rule.Declaration;
import org.drools.runtime.rule.RuleContext;
import org.drools.spi.KnowledgeHelper;
import org.mvel2.DataConversion;
import org.mvel2.MVEL;
import org.mvel2.ParserContext;
import org.mvel2.compiler.AbstractParser;
import org.mvel2.compiler.ExpressionCompiler;

public class MVELCompilationUnit
    implements
    Externalizable {

    private static final long serialVersionUID = 510L;

    private String                          name;
    
    private String                          expression;

    private String[]                        pkgImports;
    private String[]                        importClasses;
    private String[]                        importMethods;
    private String[]                        importFields;
    private String[]                        globalIdentifiers;

    private Declaration[]                   previousDeclarations;
    private Declaration[]                   localDeclarations;
    private String[]                        otherIdentifiers;

    private String[]                        inputIdentifiers;
    private String[]                        inputTypes;

    private String[]                        shadowIdentifiers;

    private int                             languageLevel;
    private boolean                         strictMode;

    private Map<String, Class>              resolvedInputs;

    private static Map                      interceptors  = new HashMap( 2 );
    static {
        interceptors.put( "Modify",
                          new ModifyInterceptor() );
    }
    
    static {
        //for handling dates as string literals
        DataConversion.addConversionHandler( Date.class,
                                             new MVELDateCoercion() );
        DataConversion.addConversionHandler( Calendar.class,
                                             new MVELCalendarCoercion() );
    }    

    private static final Map<String, Class> primitivesMap = new HashMap<String, Class>();
    static {
        primitivesMap.put( "int",
                           int.class );
        primitivesMap.put( "boolean",
                           boolean.class );
        primitivesMap.put( "float",
                           float.class );
        primitivesMap.put( "long",
                           long.class );
        primitivesMap.put( "short",
                           short.class );
        primitivesMap.put( "byte",
                           byte.class );
        primitivesMap.put( "double",
                           double.class );
        primitivesMap.put( "char",
                           char.class );
    }

    public static final Object              COMPILER_LOCK = new Object();

    public MVELCompilationUnit() {
    }

    public MVELCompilationUnit(String name, 
                               String expression,
                               String[] pkgImports,
                               String[] importClasses,
                               String[] importMethods,
                               String[] importFields,
                               String[] globalIdentifiers,
                               Declaration[] previousDeclarations,
                               Declaration[] localDeclarations,
                               String[] otherIdentifiers,
                               String[] inputIdentifiers,
                               String[] inputTypes,
                               int languageLevel,
                               boolean strictMode) {
        this.name = name;
        this.expression = expression;

        this.pkgImports = pkgImports;
        this.importClasses = importClasses;
        this.importMethods = importMethods;
        this.importFields = importFields;
        this.globalIdentifiers = globalIdentifiers;

        this.previousDeclarations = previousDeclarations;
        this.localDeclarations = localDeclarations;
        this.otherIdentifiers = otherIdentifiers;

        this.inputIdentifiers = inputIdentifiers;
        this.inputTypes = inputTypes;

        this.languageLevel = languageLevel;
        this.strictMode = strictMode;
    }       

    public String getExpression() {
        return expression;
    }

    public void setShadowIdentifiers(String[] shadowIdentifiers) {
        this.shadowIdentifiers = shadowIdentifiers;
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeUTF( name );
        
        out.writeUTF( expression );

        out.writeObject( pkgImports );
        out.writeObject( importClasses );
        out.writeObject( importMethods );
        out.writeObject( importFields );
        out.writeObject( globalIdentifiers );

        out.writeObject( previousDeclarations );
        out.writeObject( localDeclarations );
        out.writeObject( otherIdentifiers );

        out.writeObject( inputIdentifiers );
        out.writeObject( inputTypes );

        out.writeObject( shadowIdentifiers );

        out.writeInt( languageLevel );
        out.writeBoolean( strictMode );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        name = in.readUTF();
        expression = in.readUTF();

        pkgImports = (String[]) in.readObject();
        importClasses = (String[]) in.readObject();
        importMethods = (String[]) in.readObject();
        importFields = (String[]) in.readObject();
        globalIdentifiers = (String[]) in.readObject();

        previousDeclarations = (Declaration[]) in.readObject();
        localDeclarations = (Declaration[]) in.readObject();
        otherIdentifiers = (String[]) in.readObject();

        inputIdentifiers = (String[]) in.readObject();
        inputTypes = (String[]) in.readObject();

        shadowIdentifiers = (String[]) in.readObject();

        languageLevel = in.readInt();
        strictMode = in.readBoolean();
    }

    public Serializable getCompiledExpression(ClassLoader classLoader) {
        Map<String, Object> resolvedImports = new HashMap<String, Object>( importClasses.length + importMethods.length + importFields.length );
        String lastName = null;
        try {
            for ( String name : importClasses ) {
                lastName = name;
                Class cls = loadClass( classLoader,
                                       name );
                resolvedImports.put( cls.getSimpleName(),
                                     cls );
            }

            for ( String name : importMethods ) {
                lastName = name;
                int lastDot = name.lastIndexOf( '.' );
                String className = name.substring( 0,
                                                   lastDot );
                Class cls = loadClass( classLoader,
                                       className );

                String methodName = name.substring( lastDot + 1 );
                Method method = null;
                for ( Method item : cls.getMethods() ) {
                    if ( methodName.equals( item.getName() ) ) {
                        method = item;
                    }
                }
                resolvedImports.put( method.getName(),
                                     method );
            }

            for ( String name : importFields ) {
                int lastDot = name.lastIndexOf( '.' );
                String className = name.substring( 0,
                                                   lastDot );
                Class cls = loadClass( classLoader,
                                       className );

                String fieldName = name.substring( lastDot + 1 );
                Field field = cls.getField( fieldName );

                resolvedImports.put( field.getName(),
                                     field );
            }
        } catch ( ClassNotFoundException e ) {
            e.printStackTrace();
            throw new RuntimeDroolsException( "Unable to resolve import '" + lastName + "'" );
        } catch ( SecurityException e ) {
            e.printStackTrace();
            throw new RuntimeDroolsException( "Unable to resolve import '" + lastName + "'" );
        } catch ( NoSuchFieldException e ) {
            e.printStackTrace();
            throw new RuntimeDroolsException( "Unable to resolve import '" + lastName + "'" );
        }

        final ParserContext parserContext = new ParserContext( resolvedImports,
                                                               null,
                                                               name );
        parserContext.getParserConfiguration().setClassLoader( classLoader );

        for ( String pkgImport : this.pkgImports ) {
            parserContext.addPackageImport( pkgImport );
        }

        parserContext.setInterceptors( interceptors );
        parserContext.setStrongTyping( strictMode );
        //parserContext.setStrictTypeEnforcement( strictMode );

        resolvedInputs = new HashMap<String, Class>( inputIdentifiers.length );

        parserContext.addInput( "drools",
                                KnowledgeHelper.class );

		resolvedInputs.put( "drools",
		                    KnowledgeHelper.class );
		
		String lastIdentifier = null;
        String lastType = null;
        try {
            for ( int i = 0, length = inputIdentifiers.length; i < length; i++ ) {
                lastIdentifier = inputIdentifiers[i];
                lastType = inputTypes[i];
                Class cls = loadClass( classLoader,
                                       inputTypes[i] );
                resolvedInputs.put( inputIdentifiers[i],
                                    cls );
                parserContext.addInput( inputIdentifiers[i],
                                        cls );
            }
        } catch ( ClassNotFoundException e ) {
            e.printStackTrace();
            throw new RuntimeDroolsException( "Unable to resolve class '" + lastType + "' for identifier '" + lastIdentifier );
        }

        if ( parserContext.getInputs().get( "kcontext" ) == null)  {

        	parserContext.addInput( "kcontext",
	                                RuleContext.class );
	
	        resolvedInputs.put( "kcontext",
	                            RuleContext.class );

        }
        
        return compile( expression,
                        classLoader,
                        parserContext,
                        languageLevel );
    }

    public DroolsMVELFactory getFactory() {
        Map<String, Class> resolvedGlobals = null;
        if ( inputIdentifiers != null ) {
            resolvedGlobals = new HashMap<String, Class>( inputIdentifiers.length );
            for ( int i = 0, length = globalIdentifiers.length; i < length; i++ ) {
                String identifier = globalIdentifiers[i];
                resolvedGlobals.put( identifier,
                                     resolvedInputs.get( identifier ) );
            }
        }

        Map<String, Declaration> previousDeclarationsMap = null;
        if ( previousDeclarations != null ) {
            previousDeclarationsMap = new HashMap<String, Declaration>( previousDeclarations.length );
            for ( Declaration declr : previousDeclarations ) {
                previousDeclarationsMap.put( declr.getIdentifier(),
                                             declr );
            }
        }

        Map<String, Declaration> localDeclarationsMap = null;
        if ( localDeclarations != null ) {
            localDeclarationsMap = new HashMap<String, Declaration>( localDeclarations.length );
            for ( Declaration declr : localDeclarations ) {
                localDeclarationsMap.put( declr.getIdentifier(),
                                          declr );
            }
        }

        DroolsMVELFactory factory = null;
        if ( shadowIdentifiers == null ) {

            factory = new DroolsMVELFactory( previousDeclarationsMap,
                                             localDeclarationsMap,
                                             resolvedGlobals,
                                             inputIdentifiers );
        } else {
            Set<String> set = new HashSet<String>( shadowIdentifiers.length );
            for ( String string  : shadowIdentifiers ) {
                set.add( string );
            }
            factory = new DroolsMVELShadowFactory( previousDeclarationsMap,
                                                   localDeclarationsMap,
                                                   resolvedGlobals,
                                                   inputIdentifiers,
                                                   set );
        }

        return factory;
    }

    public Serializable compile(final String text,
                                final ClassLoader classLoader,
                                final ParserContext parserContext,
                                final int languageLevel) {
    	MVEL.COMPILER_OPT_ALLOW_NAKED_METH_CALL = true;
    	
        ExpressionCompiler compiler = new ExpressionCompiler( text.trim() );

        if ( MVELDebugHandler.isDebugMode() ) {
            parserContext.setDebugSymbols( true );
        }

        synchronized ( COMPILER_LOCK ) {
            ClassLoader tempClassLoader = Thread.currentThread().getContextClassLoader();

            Thread.currentThread().setContextClassLoader( classLoader );

            AbstractParser.setLanguageLevel( languageLevel );
            Serializable expr = null;
            try {
                expr = compiler.compile( parserContext );
            } finally {
                // make sure that in case of exceptions the context classloader is properly restored
                Thread.currentThread().setContextClassLoader( tempClassLoader );
            }

            return expr;
        }
    }

    private Class loadClass(ClassLoader classLoader,
                            String className) throws ClassNotFoundException {
        Class cls = primitivesMap.get( className );
        if ( cls == null ) {
            cls = classLoader.loadClass( className );
        }
        return cls;

    }

    public void replaceDeclaration(Declaration declaration,
                                   Declaration resolved) {
        if( previousDeclarations != null ) {
            for( int i = 0; i < previousDeclarations.length; i++ ) {
                if( previousDeclarations[i].equals( declaration ) ) {
                    previousDeclarations[i] = resolved; 
                }
            }
        }
        if( localDeclarations != null ) {
            for( int i = 0; i < localDeclarations.length; i++ ) {
                if( localDeclarations[i].equals( declaration ) ) {
                    localDeclarations[i] = resolved; 
                }
            }
        }
    }
}
