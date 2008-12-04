package org.drools.io;

import org.drools.builder.KnowledgeType;

public interface InternalResource extends Resource {
    boolean isFromDirectory();

    void setFromDirectory(boolean fromDirectory);

    void setKnowledgeType(KnowledgeType knowledgeType);
    KnowledgeType getKnowledgeType();

}
