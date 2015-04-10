package org.kie.internal.query;

import org.kie.internal.query.data.QueryData;

public interface QueryModificationService {

    public void addTablesToQuery(StringBuilder queryBuilder, QueryData queryData);
    
    public void addCriteriaToQuery(QueryData queryData, QueryAndParameterAppender existingQueryClauseAppender);

}
