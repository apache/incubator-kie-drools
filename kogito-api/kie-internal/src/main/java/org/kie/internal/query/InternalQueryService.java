package org.kie.internal.query;

import java.util.List;

public interface InternalQueryService {

    // The query methods should not be available in any public API's
    public <T,R> List<R> query(Object queryWhere, Class<T> queryType, Class<R> resultType);

}
