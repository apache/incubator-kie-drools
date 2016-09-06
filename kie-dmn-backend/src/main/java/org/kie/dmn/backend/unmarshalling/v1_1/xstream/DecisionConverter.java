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
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import org.kie.dmn.feel.model.v1_1.Decision;
import org.kie.dmn.feel.model.v1_1.Expression;
import org.kie.dmn.feel.model.v1_1.InformationItem;
import org.kie.dmn.feel.model.v1_1.InformationRequirement;

public class DecisionConverter
        extends DRGElementConverter {
    public static final String QUESTION        = "question";
    public static final String ALLOWED_ANSWERS = "allowedAnswers";

    public DecisionConverter(XStream xstream) {
        super( xstream );
    }

    public boolean canConvert(Class clazz) {
        return clazz.equals( Decision.class );
    }

    @Override
    protected void assignChildElement(Object parent, String nodeName, Object child) {
        Decision dec = (Decision) parent;
        if ( child instanceof InformationRequirement ) {
            dec.getInformationRequirement().add( (InformationRequirement) child );
        } else if ( child instanceof InformationItem ) {
            dec.setVariable( (InformationItem) child );
        } else if ( child instanceof Expression ) {
            dec.setExpression( (Expression) child );
        } else {
            super.assignChildElement( dec, nodeName, child );
        }
    }

    @Override
    protected void assignAttributes(HierarchicalStreamReader reader, Object parent) {
        super.assignAttributes( reader, parent );
        Decision dec = (Decision) parent;

        String question = reader.getAttribute( QUESTION );
        String allowedAnswers = reader.getAttribute( ALLOWED_ANSWERS );

        dec.setQuestion( question );
        dec.setAllowedAnswers( allowedAnswers );
    }

    @Override
    protected Object createModelObject() {
        return new Decision();
    }
}
