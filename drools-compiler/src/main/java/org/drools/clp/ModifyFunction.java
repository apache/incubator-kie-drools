package org.drools.clp;

public class ModifyFunction extends BaseFunction implements Function {
    private static final String name = "+";
    
    private VariableValueHandler variable;
    
    private SlotNameValuePair[] pairs;
    
    public ModifyFunction(VariableValueHandler variable) {
        this.variable = variable;
    }
    
    public void setVariableValueHandler(VariableValueHandler variable) {
        this.variable = variable;
    }        
    
    public ModifyFunction() {
    }
    
    public void addParameter(ValueHandler valueHandler) {
        // we implement here rather than inheriting to save a runtime cast 
        SlotNameValuePair[] temp = new SlotNameValuePair[ pairs.length ];
        System.arraycopy( this.pairs, 0, temp, 0, this.pairs.length -1 );
        temp[ temp.length - 1] = ( SlotNameValuePair) valueHandler;
        this.pairs = temp;          
    }    

    public Object getValue(ExecutionContext context) {
//        ValueHandler[] args = BaseFunction.resolveParameters( getParameters(), context );
//        BigDecimal bdval = new BigDecimal(0);        
        
        for ( int i = 0, length = this.pairs.length; i < length; i++ ) {
            
        }
                
        return null; //bdval;
    }

    public String getName() {
        return name;
    }

}
