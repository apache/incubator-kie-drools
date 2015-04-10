package org.jbpm.query.jpa.service;

import org.jbpm.query.jpa.impl.QueryAndParameterAppender;
import org.kie.internal.query.data.QueryData;

public interface QueryModificationService {

    public void addTablesToQuery(StringBuilder queryBuilder, QueryData queryData);
    
    public void addCriteriaToQuery(QueryData queryData, QueryAndParameterAppender existingQueryClauseAppender);

}
