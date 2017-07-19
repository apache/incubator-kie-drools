/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.compiler;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.ast.InputDataNodeImpl;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.model.v1_1.DRGElement;
import org.kie.dmn.model.v1_1.InputData;

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
            DMNType type = compiler.resolveTypeRef( model, idn, de, input.getVariable(), input.getVariable().getTypeRef() );
            idn.setType( type );
        } else {
            idn.setType( DMNTypeRegistry.UNKNOWN );
            DMNCompilerHelper.reportMissingVariable( model, de, input, Msg.MISSING_VARIABLE_FOR_INPUT );
        }
        model.addInput( idn );
    }
}