package org.kie.internal.query;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryContext extends org.kie.api.runtime.query.QueryContext {

    private static final long serialVersionUID = -3174717972613778773L;

    public QueryContext() {
        super();
    }

    public QueryContext(Integer offset, Integer count, String orderBy, boolean asc) {
        super(offset, count, orderBy, asc);
    }

    public QueryContext(Integer offset, Integer count) {
        super(offset, count);
    }

    public QueryContext(org.kie.api.runtime.query.QueryContext queryContext) {
        super(queryContext);
    }

    public QueryContext(String orderBy, boolean asc) {
        super(orderBy, asc);
    }


}
