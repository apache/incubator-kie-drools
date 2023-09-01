package org.drools.compiler.compiler;

import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.drl.parser.DroolsError;
import org.kie.internal.jci.CompilationProblem;

public class FunctionError extends DroolsError {
    final private FunctionDescr functionDescr;
    final private Object        object;
    final private String        message;
    private int[]               errorLines;

    public FunctionError(final FunctionDescr functionDescr,
                         final Object object,
                         final String message) {
        super(functionDescr.getResource());
        this.functionDescr = functionDescr;
        this.object = object;
        this.message = createMessage( message );
    }

    @Override
    public String getNamespace() {
        return functionDescr.getNamespace();
    }

    public FunctionDescr getFunctionDescr() {
        return this.functionDescr;
    }

    public Object getObject() {
        return this.object;
    }
    
    public int[] getLines() {
        return errorLines;
    }

    public String getMessage() {
        return this.message;
    }
    
    public String toString() {
        return this.message;
    }
    
    private String createMessage( String message ) {
        StringBuilder detail = new StringBuilder();
        if( object instanceof CompilationProblem[] ) {
            CompilationProblem[] cp = (CompilationProblem[]) object;
            this.errorLines = new int[cp.length];
            for( int i = 0; i < cp.length ; i ++ ) {
               this.errorLines[i] = cp[i].getStartLine() - this.functionDescr.getOffset() + this.getFunctionDescr().getLine() - 1;
               detail.append( this.functionDescr.getName() );
               detail.append( " (line:" );
               detail.append( this.errorLines[i] );
               detail.append( "): " );
               detail.append( cp[i].getMessage() );
               detail.append( "\n" );
            }
        } else if( object instanceof Exception) {
            Exception ex = (Exception) object;
            this.errorLines = new int[1];
            this.errorLines[0] = functionDescr.getLine();
            detail.append( " (line:" );
            detail.append( this.errorLines[0] );
            detail.append( "): " );
            detail.append( message );
            detail.append( " " );
            detail.append( ex.getClass().getName() );
            if( ex.getMessage() != null ) {
                detail.append( ": " );
                detail.append( ex.getMessage() );
            }
        } else {
            this.errorLines = new int[1];
            this.errorLines[0] = functionDescr.getLine();
        }
        return "[ function "+functionDescr.getName() + detail.toString()+" ]";
    }

}
