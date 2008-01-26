package org.drools.clp.mvel;

import java.io.Serializable;
import java.util.Map;

import org.mvel.MVEL;
import org.mvel.compiler.CompiledExpression;
import org.mvel.compiler.ExpressionCompiler;
import org.mvel.util.CompilerTools;

public class DeffunctionFunction implements Function {
    private static final String name = "deffunction";

    public String getName() {
        return name;
    }
    
    public void dump(LispForm lispForm, Appendable appendablex, MVELClipsContext context) {
        SExpression[] sExpressions = lispForm.getSExpressions();
        
        String functionName = ( (LispAtom) sExpressions[1]).getValue().trim();
        
        Appendable functionText = new StringBuilderAppendable();
        
        functionName = functionName.substring( 1, functionName.length() -1 );
        
        functionText.append( "function " + functionName + "(" );
        
        LispForm params = (LispForm) sExpressions[2];
        for ( int i = 0, length =  params.getSExpressions().length; i < length; i++ ) {
            functionText.append( ( (LispAtom) params.getSExpressions()[i]).getValue() );
            if ( i < length-1 ) {
                functionText.append( ", " );
            }            
        }
        
        functionText.append( ") {\n" );
        FunctionHandlers.dump( sExpressions[3], functionText, context );
        functionText.append( "}" );
        
        ExpressionCompiler compiler = new ExpressionCompiler( functionText.toString() );
        Serializable s1 = compiler.compile();
        Map<String, org.mvel.ast.Function> map = CompilerTools.extractAllDeclaredFunctions((CompiledExpression) s1);    
        for ( org.mvel.ast.Function function : map.values() ) {
            context.addFunction( function );
        }
    }
}
