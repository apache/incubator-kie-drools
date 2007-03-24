package org.drools.clp;

import java.util.Map;

public class FunctionCaller extends BaseValueHandler {
    private Function function;
    
    protected ValueHandler[] parameters;
    
    public FunctionCaller() {
        this( null );
    }
    
    public FunctionCaller(Function function) {
        this.function = function;
    }
    
    public void setFunction(Function function) {
        this.function = function;
    }
    
    public Function getFunction() {
        return this.function;
    }
    
    public String getName() {
        return ( this.function == null ) ? null : this.function.getName();
    }    
    
    public LispList createList(int index) {
        return this.function.createList( index );
    }
    
    public void addParameter(ValueHandler valueHandler) {
        if (this.parameters == null) {
            this.parameters = new ValueHandler[] { valueHandler};
        } else {
            ValueHandler[] temp =  new ValueHandler[ parameters.length + 1 ];
            System.arraycopy( this.parameters, 0, temp, 0, this.parameters.length );
            temp[ temp.length - 1] = valueHandler;
            this.parameters = temp;             
        }                
    }
    
    public ValueHandler[] getParameters() {
        return this.parameters;
    }    
    
    public Object getValue(ExecutionContext context) {        
        return function.execute( parameters, context );
    }

    public void setValue(ExecutionContext context,
                         Object object) {
        throw new RuntimeException( "You cannot set the value on a Function" );        
    }
    
  public void replaceTempTokens(Map variables) {
      for ( int i = 0, length = this.parameters.length; i < length; i++ ) {
          if ( this.parameters[i] instanceof TempTokenVariable ) {
              TempTokenVariable var = ( TempTokenVariable ) this.parameters[i]; 
              this.parameters[i] = ( ValueHandler ) variables.get( var.getIdentifier() );
          } else if ( this.parameters[i] instanceof FunctionCaller ) {
              ((FunctionCaller)parameters[i]).replaceTempTokens( variables );
          } else if ( this.parameters[i] instanceof ListValueHandler ) {
              ((ListValueHandler)parameters[i]).replaceTempTokens( variables );
          }
      }  
  }    

  public String toString() {
      return "[FunctionCaller " + this.function + "]";
  }
  
}
