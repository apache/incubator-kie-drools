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

import org.kie.dmn.api.core.DMNMessage;
import org.kie.dmn.api.feel.runtime.events.FEELEvent;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.core.util.Msg;
import org.kie.dmn.core.util.MsgUtil;
import org.kie.dmn.feel.parser.feel11.FEELParser;
import org.kie.dmn.model.v1_1.NamedElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class DMNCompilerHelper {
    private static final Logger logger = LoggerFactory.getLogger( DMNCompilerHelper.class );

    public static boolean checkVariableName(DMNModelImpl model, NamedElement element, String variableName) {
        List<FEELEvent> errors = FEELParser.checkVariableName( variableName );
        if ( ! errors.isEmpty() ) {
            MsgUtil.reportMessage( logger,
                                   DMNMessage.Severity.ERROR,
                                   element,
                                   model,
                                   null,
                                   errors.get( 0 ),
                                   Msg.INVALID_NAME,
                                   variableName,
                                   errors.get( 0 ).getMessage() );
            return false;
        }
        return true;
    }

}
