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
