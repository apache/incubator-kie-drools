package org.drools.drl.ast.descr;

import org.kie.api.io.Resource;

import java.util.List;

public class PredicateDescr extends RestrictionDescr {
    private static final long serialVersionUID = 510l;
    private Object            content;

    private String[]          declarations;

    private String            classMethodName;

    private List<String>      parameters;

    public PredicateDescr() { }

    public PredicateDescr(final Object text) {
        this(null, text);
    }

    public PredicateDescr(final Resource resource, final Object text) {
        this.content = text;
        setResource(resource);
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

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "predicate '" + content + "'";
    }


    public void copyParameters( BaseDescr base ) {
        if ( base instanceof RelationalExprDescr ) {
            setParameters( ((RelationalExprDescr) base).getParameters() );
        }
    }
}
