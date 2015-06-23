/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.impl.adapters;

import org.drools.builder.KnowledgeBuilderError;
import org.kie.internal.builder.KnowledgeBuilderErrors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class KnowledgeBuilderErrorsAdapter extends ArrayList<KnowledgeBuilderError> implements org.drools.builder.KnowledgeBuilderErrors {

    public KnowledgeBuilderErrorsAdapter(KnowledgeBuilderErrors delegate) {
        super(adaptErrors(delegate));
    }

    private static List<KnowledgeBuilderError> adaptErrors(KnowledgeBuilderErrors errors) {
        List<KnowledgeBuilderError> result = new ArrayList<KnowledgeBuilderError>();
        for (org.kie.internal.builder.KnowledgeBuilderError error : errors) {
            result.add(new KnowledgeBuilderErrorAdapter(error));
        }
        return result;
    }
}
