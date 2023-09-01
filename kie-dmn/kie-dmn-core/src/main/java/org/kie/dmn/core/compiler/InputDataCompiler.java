package org.kie.dmn.core.compiler;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.ast.InputDataNodeImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.InputData;

public class InputDataCompiler implements DRGElementCompiler {
    @Override
    public boolean accept(DRGElement de) {
        return de instanceof InputData;
    }
    @Override
    public void compileNode(DRGElement de, DMNCompilerImpl compiler, DMNModelImpl model) {
        InputData input = (InputData) de;
        InputDataNodeImpl idn = new InputDataNodeImpl( input );
        if ( input.getVariable() != null ) {
            DMNCompilerHelper.checkVariableName( model, input, input.getName() );
            DMNType type = compiler.resolveTypeRef(model, de, input.getVariable(), input.getVariable().getTypeRef());
            idn.setType( type );
        } else {
            idn.setType(model.getTypeRegistry().unknown());
            DMNCompilerHelper.reportMissingVariable( model, de, input, Msg.MISSING_VARIABLE_FOR_INPUT );
        }
        model.addInput( idn );
    }
}