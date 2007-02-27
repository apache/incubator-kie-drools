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
import org.codehaus.jfdi.interpreter.TypeResolver;
import org.drools.RuntimeDroolsException;
import org.drools.lang.descr.FunctionDescr;
import org.drools.rule.LineMappings;
import org.drools.rule.Package;
import org.drools.rule.builder.FunctionBuilder;

public class JavaFunctionBuilder
    implements
    FunctionBuilder {

    private static final StringTemplateGroup functionGroup = new StringTemplateGroup( new InputStreamReader( FunctionBuilder.class.getResourceAsStream( "javaFunction.stg" ) ),
                                                                                      AngleBracketTemplateLexer.class );

    public JavaFunctionBuilder() {

    }

    /* (non-Javadoc)
     * @see org.drools.rule.builder.dialect.java.JavaFunctionBuilder#build(org.drools.rule.Package, org.drools.lang.descr.FunctionDescr, org.codehaus.jfdi.interpreter.TypeResolver, java.util.Map)
     */
    public String build(final Package pkg,
                        final FunctionDescr functionDescr,
                        final TypeResolver typeResolver,
                        final Map lineMappings) {
        final StringTemplate st = JavaFunctionBuilder.functionGroup.getInstanceOf( "function" );

        st.setAttribute( "package",
                         pkg.getName() );

        st.setAttribute( "imports",
                         pkg.getImports() );

        st.setAttribute( "className",
                         ucFirst( functionDescr.getName() ) );
        st.setAttribute( "methodName",
                         functionDescr.getName() );

        st.setAttribute( "returnType",
                         functionDescr.getReturnType() );

        st.setAttribute( "parameterTypes",
                         functionDescr.getParameterTypes() );

        st.setAttribute( "parameterNames",
                         functionDescr.getParameterNames() );

        Map params = new HashMap();
        List names = functionDescr.getParameterNames();
        List types = functionDescr.getParameterTypes();
        try {
            for ( int i = 0, size = names.size(); i < size; i++ ) {
                params.put( names.get( i ),
                            typeResolver.resolveType( (String) types.get( i ) ) );
            }
        } catch ( ClassNotFoundException e ) {
            // todo : must be a better way so we don't have to try/catch each resolveType call
            throw new RuntimeDroolsException( e );
        }

        st.setAttribute( "text",
                         functionDescr.getText() );

        String text = st.toString();

        BufferedReader reader = new BufferedReader( new StringReader( text ) );
        String line = null;
        String lineStartsWith = "    public static " + functionDescr.getReturnType() + " " + functionDescr.getName();
        int offset = 0;
        try {
            while ( (line = reader.readLine()) != null ) {
                offset++;
                if ( line.startsWith( lineStartsWith ) ) {
                    break;
                }
            }
            functionDescr.setOffset( offset );
        } catch ( IOException e ) {
            // won't ever happen, it's just reading over a string.
            throw new RuntimeDroolsException( "Error determining start offset with function" );
        }

        String name = pkg.getName() + "." + ucFirst( functionDescr.getName() );
        LineMappings mapping = new LineMappings( name );
        mapping.setStartLine( functionDescr.getLine() );
        mapping.setOffset( functionDescr.getOffset() );
        lineMappings.put( name,
                          lineMappings );

        return text;

    }

    private String ucFirst(final String name) {
        return name.toUpperCase().charAt( 0 ) + name.substring( 1 );
    }

}