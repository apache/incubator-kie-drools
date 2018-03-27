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

package org.kie.dmn.validation;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNRuntime;
import org.kie.dmn.core.util.DMNRuntimeUtil;
import org.kie.dmn.model.v1_1.Definitions;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

public abstract class AbstractValidatorTest {

    protected static DMNValidator validator;

    @BeforeClass
    public static void init() {
        validator = DMNValidatorFactory.newValidator();
    }

    @AfterClass
    public static void dispose() {
        validator.dispose();
    }

    protected Reader getReader(final String resourceFileName ) {
        return new InputStreamReader( getClass().getResourceAsStream( resourceFileName ) );
    }

    protected File getFile(final String resourceFileName ) {
        return new File(this.getClass().getResource(resourceFileName).getFile());
    }

    protected Definitions getDefinitions(final String resourceName, final String namespace, final String modelName ) {
        final DMNRuntime runtime = DMNRuntimeUtil.createRuntime(resourceName, this.getClass() );
        final DMNModel dmnModel = runtime.getModel(namespace, modelName );
        assertThat( dmnModel, notNullValue() );

        final Definitions definitions = dmnModel.getDefinitions();
        assertThat( definitions, notNullValue() );
        return definitions;
    }
}
