package org.drools.drl.ast.descr;

public class ReturnValueRestrictionDescr extends EvaluatorBasedRestrictionDescr {

    private static final long serialVersionUID = 510l;
    private Object            content;
    private String[]          declarations;
    private String            classMethodName;

    public ReturnValueRestrictionDescr(){
    }

    public ReturnValueRestrictionDescr(String evaluator,
                                       RelationalExprDescr relDescr,
                                       Object content) {
        super( evaluator,
               relDescr.isNegated(),
               relDescr.getParametersText() );
        this.content = content;
        setResource( relDescr.getResource() );
    }

    public String getClassMethodName() {
        return this.classMethodName;
    }

    public void setClassMethodName(final String classMethodName) {
        this.classMethodName = classMethodName;
    }

    public Object getContent() {
        return this.content;
    }

    public void setContent(final Object text) {
        this.content = text;
    }

    public void setDeclarations(final String[] declarations) {
        this.declarations = declarations;
    }

    public String[] getDeclarations() {
        return this.declarations;
    }

    public String toString() {
        return "[ReturnValue: " + super.toString() + " " + this.content + "]";
    }
}
