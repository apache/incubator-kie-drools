/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

import org.kie.api.io.Resource;
import org.kie.dmn.backend.unmarshalling.v1_1.DefaultUnmarshaller;
import org.kie.dmn.core.api.DMNCompiler;
import org.kie.dmn.core.api.DMNModel;
import org.kie.dmn.core.impl.DMNModelImpl;
import org.kie.dmn.feel.model.v1_1.Definitions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Reader;

public class DMNCompilerImpl implements DMNCompiler {

    private static final Logger logger = LoggerFactory.getLogger( DMNCompilerImpl.class );

    @Override
    public DMNModel compile(Resource resource) {
        try {
            Definitions dmndefs = new DefaultUnmarshaller().unmarshal( resource.getReader() );
            if (dmndefs != null) {
                DMNModel model = new DMNModelImpl( dmndefs );
                return model;
            }
        } catch( Exception e ) {
            logger.error( "Error compiling model for resource '"+resource.getSourcePath()+"'", e );
        }
        return null;
    }

    @Override
    public DMNModel compile(Reader source) {
        return null;
    }
}
