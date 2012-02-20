package org.drools.rule.builder.dialect.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.drools.RuntimeDroolsException;
import org.drools.base.TypeResolver;
import org.drools.builder.KnowledgeBuilderResult;
import org.drools.compiler.DroolsError;
import org.drools.compiler.FunctionError;
import org.drools.core.util.StringUtils;
import org.drools.lang.descr.FunctionDescr;
import org.drools.rule.LineMappings;
import org.drools.rule.Package;
import org.drools.rule.builder.FunctionBuilder;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.mvel2.templates.TemplateRuntime;


public class JavaFunctionBuilder
    implements
    FunctionBuilder {

    //    private static final StringTemplateGroup functionGroup = new StringTemplateGroup( new InputStreamReader( JavaFunctionBuilder.class.getResourceAsStream( "javaFunction.stg" ) ),
    //                                                                                      AngleBracketTemplateLexer.class );

    private static final String template = StringUtils.readFileAsString( new InputStreamReader( JavaFunctionBuilder.class.getResourceAsStream( "javaFunction.mvel" ) ) );

    public JavaFunctionBuilder() {

    }

    /* (non-Javadoc)
     * @see org.drools.rule.builder.dialect.java.JavaFunctionBuilder#build(org.drools.rule.Package, org.drools.lang.descr.FunctionDescr, org.codehaus.jfdi.interpreter.TypeResolver, java.util.Map)
     */
    public String build(final Package pkg,
                        final FunctionDescr functionDescr,
                        final TypeResolver typeResolver,
                        final Map<String, LineMappings> lineMappings,
                        final List<KnowledgeBuilderResult> errors) {

        final Map<String, Object> vars = new HashMap<String, Object>();

        vars.put( "package",
                  pkg.getName() );

        vars.put( "imports",
                  pkg.getImports().keySet() );

        final List<String> staticImports = new LinkedList<String>();
        for (String staticImport : pkg.getStaticImports()) {
            if ( !staticImport.endsWith( functionDescr.getName() ) ) {
                staticImports.add( staticImport );
            }
        }

        vars.put( "staticImports",
                  staticImports );

        vars.put( "className",
                  StringUtils.ucFirst( functionDescr.getName() ) );

        vars.put( "methodName",
                  functionDescr.getName() );

        vars.put( "returnType",
                  functionDescr.getReturnType() );

        vars.put( "parameterTypes",
                  functionDescr.getParameterTypes() );

        vars.put( "parameterNames",
                  functionDescr.getParameterNames() );
        
        vars.put("hashCode",
                functionDescr.getText().hashCode() );

        // Check that all the parameters are resolvable
        final Map<String, Class<?>> params = new HashMap();
        final List<String> names = functionDescr.getParameterNames();
        final List<String> types = functionDescr.getParameterTypes();
        for ( int i = 0, size = names.size(); i < size; i++ ) {
            try {
                params.put( names.get( i ),
                            typeResolver.resolveType( (String) types.get( i ) ) );
            } catch ( final ClassNotFoundException e ) {
                errors.add( new FunctionError( functionDescr,
                                               e,
                                               "Unable to resolve type "+types.get( i )+" while building function." ) );
                break;
            }
        }

        vars.put( "text",
                  functionDescr.getText() );

        final String text = String.valueOf(TemplateRuntime.eval( template, null, new MapVariableResolverFactory(vars)));

        final BufferedReader reader = new BufferedReader( new StringReader( text ) );
        String line = null;
        final String lineStartsWith = "    public static " + functionDescr.getReturnType() + " " + functionDescr.getName();
        int offset = 0;
        try {
            while ( (line = reader.readLine()) != null ) {
                offset++;
                if ( line.startsWith( lineStartsWith ) ) {
                    break;
                }
            }
            functionDescr.setOffset( offset );
        } catch ( final IOException e ) {
            // won't ever happen, it's just reading over a string.
            throw new RuntimeDroolsException( "Error determining start offset with function" );
        }

        final String name = pkg.getName() + "." + StringUtils.ucFirst( functionDescr.getName() );
        final LineMappings mapping = new LineMappings( name );
        mapping.setStartLine( functionDescr.getLine() );
        mapping.setOffset( functionDescr.getOffset() );
        lineMappings.put( name,
                          mapping );

        return text;

    }

}
