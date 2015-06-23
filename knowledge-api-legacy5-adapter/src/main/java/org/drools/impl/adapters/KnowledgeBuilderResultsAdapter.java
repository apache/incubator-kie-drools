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

import org.drools.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.KnowledgeBuilderResults;

import java.util.ArrayList;
import java.util.List;

public class KnowledgeBuilderResultsAdapter extends ArrayList<KnowledgeBuilderResult> implements org.drools.builder.KnowledgeBuilderResults {

    public KnowledgeBuilderResultsAdapter(KnowledgeBuilderResults delegate) {
        super(adaptErrors(delegate));
    }

    private static List<KnowledgeBuilderResult> adaptErrors(KnowledgeBuilderResults results) {
        List<KnowledgeBuilderResult> result = new ArrayList<KnowledgeBuilderResult>();
        for (org.kie.internal.builder.KnowledgeBuilderResult res : results) {
            result.add(new KnowledgeBuilderResultAdapter(res));
        }
        return result;
    }
}
