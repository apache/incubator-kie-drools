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
package org.kie.dmn.backend.unmarshalling.v1_1;

import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.Ignore;
import org.junit.Test;
import org.kie.dmn.feel.model.v1_1.Definitions;
import org.kie.dmn.unmarshalling.v1_1.Unmarshaller;

import static org.junit.Assert.*;

public class DMNXMLLoader {

    @Test
    @Ignore("No unmarshaller implemented")
    public void testLoadingExample() {
        final Unmarshaller unmarshaller = new DefaultUnmarshaller();

        final InputStream is = this.getClass().getResourceAsStream( "/src/test/resources/ch11example.xml" );
        final InputStreamReader isr = new InputStreamReader( is );
        final Object o = unmarshaller.unmarshal( isr );

        final Definitions root = (Definitions) o;

        assertNotNull( root );
    }

    @Test
    @Ignore("No unmarshaller implemented")
    public void testLoadingDishDecision() {
        final Unmarshaller unmarshaller = new DefaultUnmarshaller();

        final InputStream is = this.getClass().getResourceAsStream( "/src/test/resources/dish-decision.xml" );
        final InputStreamReader isr = new InputStreamReader( is );
        final Object o = unmarshaller.unmarshal( isr );

        final Definitions root = (Definitions) o;

        assertNotNull( root );
    }

}
