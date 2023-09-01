package org.drools.model.codegen.execmodel.errors;

import org.drools.drl.parser.DroolsError;
import org.kie.internal.builder.ResultSeverity;

public class IncompatibleGetterOverloadError extends DroolsError {

    private Class klass;
    private String oldName;
    private Class oldType;
    private String newName;
    private Class newType;

    public IncompatibleGetterOverloadError( Class klass, String oldName, Class oldType, String newName, Class newType ) {
        this.klass = klass;
        this.oldName = oldName;
        this.oldType = oldType;
        this.newName = newName;
        this.newType = newType;
    }

    @Override
    public ResultSeverity getSeverity() {
        return ResultSeverity.ERROR;
    }


    @Override
    public String getMessage() {
        return " Imcompatible Getter overloading detected in class " + klass.getName() + " : " + oldName + " (" + oldType + ") vs " + newName + " (" + newType + ") ";
    }


    @Override
    public int[] getLines() {
        return new int[ 0 ];
    }
}
