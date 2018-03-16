/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.facttemplate;

import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.facttemplates.Fact;
import org.drools.core.facttemplates.FactTemplate;
import org.drools.core.facttemplates.FactTemplateImpl;
import org.drools.core.facttemplates.FieldTemplate;
import org.drools.core.facttemplates.FieldTemplateImpl;
import org.drools.model.Prototype;

public class FactFactory {

    public static Fact createMapBasedFact(FactTemplate factTemplate) {
        return new HashMapFactImpl( factTemplate );
    }

    public static Fact createMapBasedFact(Prototype prototype) {
        return new HashMapFactImpl( prototypeToFactTemplate( prototype, new KnowledgePackageImpl( prototype.getPackage() ) ) );
    }

    public static FactTemplate prototypeToFactTemplate( Prototype prototype, KnowledgePackageImpl pkg ) {
        FieldTemplate[] fieldTemplates = new FieldTemplate[prototype.getFields().length];
        for (int i = 0; i < prototype.getFields().length; i++) {
            fieldTemplates[i] = new FieldTemplateImpl( prototype.getFields()[i].getName(), i, prototype.getFields()[i].getType() );
        }
        return new FactTemplateImpl( pkg, prototype.getName(), fieldTemplates );
    }


}
