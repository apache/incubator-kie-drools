
package org.drools.guvnor.client.modeldriven.brl;

/**
 *
 * @author esteban
 */
public class FromAccumulateCompositeFactPattern extends FromCompositeFactPattern {
    
    public static final String USE_FUNCTION = "use_function";
    public static final String USE_CODE = "use_code";

    private IPattern sourcePattern;
    private String initCode;
    private String actionCode;
    private String reverseCode;
    private String resultCode;

    private String function;

    public FromAccumulateCompositeFactPattern() {
    }


    public String useFunctionOrCode(){
        if (this.initCode!=null && !this.initCode.trim().equals("")){
            //if the initCode is set, we must use it.
            return FromAccumulateCompositeFactPattern.USE_CODE;
        }

        //otherwise use Function. (this is the default)
        return FromAccumulateCompositeFactPattern.USE_FUNCTION;
    }

    public void clearCodeFields(){
        this.initCode = null;
        this.actionCode = null;
        this.reverseCode = null;
        this.resultCode = null;
    }
    

    public String getActionCode() {
        return actionCode;
    }

    public void setActionCode(String actionCode) {
        this.actionCode = actionCode;
    }

    public String getInitCode() {
        return initCode;
    }

    public void setInitCode(String initCode) {
        this.initCode = initCode;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getReverseCode() {
        return reverseCode;
    }

    public void setReverseCode(String reverseCode) {
        this.reverseCode = reverseCode;
    }

    public IPattern getSourcePattern() {
        return sourcePattern;
    }

    public void setSourcePattern(IPattern sourcePattern) {
        this.sourcePattern = sourcePattern;
    }

    public String getFunction() {
        return function;
    }

    public void setFunction(String function) {
        this.function = function;
    }




}
