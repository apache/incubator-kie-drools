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

package org.kie.dmn.backend.unmarshalling.v1_1.xstream;

import com.thoughtworks.xstream.XStream;
import org.kie.dmn.feel.model.v1_1.*;
import org.kie.dmn.unmarshalling.v1_1.Unmarshaller;

import java.io.Reader;
import java.io.StringReader;

public class XStreamUnmarshaller
        implements Unmarshaller {

    @Override
    public Definitions unmarshal(String xml) {
        return unmarshal( new StringReader( xml ) );
    }

    @Override
    public Definitions unmarshal(Reader isr) {
        try {
            XStream xStream = newXStream();

            Definitions def = (Definitions) xStream.fromXML( isr );

            return def;
        } catch ( Exception e ) {
            e.printStackTrace();
        }
        return null;
    }

    private XStream newXStream() {
        XStream xStream = new XStream();
        xStream.alias( "definitions", Definitions.class );
        xStream.alias( "inputData", InputData.class );
        xStream.alias( "decision", Decision.class );
        xStream.alias( "variable", InformationItem.class );
        xStream.alias( "informationRequirement", InformationRequirement.class );
        xStream.alias( "requiredInput", DMNElementReference.class );
        xStream.alias( "literalExpression", LiteralExpression.class );
        xStream.alias( "text", String.class );

        xStream.registerConverter( new DefinitionsConverter( xStream ) );
        xStream.registerConverter( new DecisionConverter( xStream ) );
        xStream.registerConverter( new InputDataConverter( xStream ) );
        xStream.registerConverter( new InformationItemConverter( xStream ) );
        xStream.registerConverter( new InformationRequirementConverter( xStream ) );
        xStream.registerConverter( new DMNElementReferenceConverter( xStream ) );
        xStream.registerConverter( new LiteralExpressionConverter( xStream ) );
        return xStream;
    }

}
