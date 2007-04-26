package org.drools.rule.builder.dialect.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;
import org.antlr.stringtemplate.language.AngleBracketTemplateLexer;
import org.drools.RuntimeDroolsException;
import org.drools.base.TypeResolver;
import org.drools.compiler.FunctionError;
import org.drools.lang.descr.FunctionDescr;
import org.drools.rule.LineMappings;
import org.drools.rule.Package;
import org.drools.rule.builder.FunctionBuilder;
import org.drools.util.StringUtils;

public class JavaFunctionBuilder
    implements
    FunctionBuilder {

    private static final StringTemplateGroup functionGroup = new StringTemplateGroup( new InputStreamReader( JavaFunctionBuilder.class.getResourceAsStream( "javaFunction.stg" ) ),
                                                                                      AngleBracketTemplateLexer.class );

    public JavaFunctionBuilder() {

    }

    /* (non-Javadoc)
     * @see org.drools.rule.builder.dialect.java.JavaFunctionBuilder#build(org.drools.rule.Package, org.drools.lang.descr.FunctionDescr, org.codehaus.jfdi.interpreter.TypeResolver, java.util.Map)
     */
    public String build(final Package pkg,
                        final FunctionDescr functionDescr,
                        final TypeResolver typeResolver,
                        final Map lineMappings,
                        final List errors) {
        final StringTemplate st = JavaFunctionBuilder.functionGroup.getInstanceOf( "function" );

        st.setAttribute( "package",
                         pkg.getName() );

        st.setAttribute( "imports",
                         pkg.getImports() );

        st.setAttribute( "className",
                         StringUtils.ucFirst( functionDescr.getName() ) );
        st.setAttribute( "methodName",
                         functionDescr.getName() );

        st.setAttribute( "returnType",
                         functionDescr.getReturnType() );

        st.setAttribute( "parameterTypes",
                         functionDescr.getParameterTypes() );

        st.setAttribute( "parameterNames",
                         functionDescr.getParameterNames() );

        final Map params = new HashMap();
        final List names = functionDescr.getParameterNames();
        final List types = functionDescr.getParameterTypes();
        try {
            for ( int i = 0, size = names.size(); i < size; i++ ) {
                params.put( names.get( i ),
                            typeResolver.resolveType( (String) types.get( i ) ) );
            }
        } catch ( final ClassNotFoundException e ) {            
            errors.add(  new FunctionError(functionDescr, e, "unable to resolve type while building function") );
        }

        st.setAttribute( "text",
                         functionDescr.getText() );

        final String text = st.toString();

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