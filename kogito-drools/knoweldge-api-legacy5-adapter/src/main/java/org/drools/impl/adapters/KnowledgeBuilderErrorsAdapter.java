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
