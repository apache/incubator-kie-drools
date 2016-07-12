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
package org.kie.dmn.feel11;

import java.io.InputStream;
import java.io.InputStreamReader;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBIntrospector;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.kie.dmn.model_1_1.TDefinitions;

import static org.junit.Assert.*;

public class DMNXMLLoader {

    @Test
    public void testLoadingExample() throws JAXBException {
        final JAXBContext jaxbContext = JAXBContext.newInstance( TDefinitions.class );
        final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        final InputStream is = this.getClass().getResourceAsStream( "/ch11example.xml" );
        final InputStreamReader isr = new InputStreamReader( is );
        final Object o = unmarshaller.unmarshal( isr );

        final TDefinitions root = (TDefinitions) JAXBIntrospector.getValue( o );

        assertNotNull( root );
    }

}
