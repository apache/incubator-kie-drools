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

package org.kie.dmn.core;

import org.drools.core.impl.StatefulKnowledgeSessionImpl;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.dmn.backend.unmarshalling.v1_1.DefaultUnmarshaller;
import org.kie.dmn.core.runtime.*;
import org.kie.dmn.feel.model.v1_1.Definitions;
import org.kie.dmn.unmarshalling.v1_1.Unmarshaller;

import java.io.InputStream;
import java.io.InputStreamReader;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class DMNRuntimeTest {

    @Test
    public void testLoadingDefinitions() {
        final Unmarshaller unmarshaller = new DefaultUnmarshaller();

        final InputStream is = this.getClass().getResourceAsStream( "0001-input-data-string.dmn" );
        final InputStreamReader isr = new InputStreamReader( is );
        final Definitions def = unmarshaller.unmarshal( isr );
    }

    @Test
    public void testGetRuntime() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kieContainer = KieHelper.getKieContainer(
                ks.newReleaseId( "org.kie", "dmn-test", "1.0" ),
                ks.getResources().newClassPathResource( "0001-input-data-string.dmn", DMNRuntimeTest.class ) );

        // the method getKieRuntime() to be moved to the public API
        DMNRuntime dmnRuntime = ((StatefulKnowledgeSessionImpl) kieContainer.newKieSession()).getKieRuntime( DMNRuntime.class );
        assertNotNull( dmnRuntime );

        DMNModel dmnModel = dmnRuntime.getModelByName( "0001-input-data-string" );

        DMNContext context = DMNFactory.newContext();
        context.set( "Full Name", "John Doe" );

        DMNResult dmnResult = dmnModel.evaluateAll( context );

        DMNContext result = dmnResult.getContext();

        assertThat( result.get( "Greeting Message" ), is( "Hello John Doe" ) );
    }




}
