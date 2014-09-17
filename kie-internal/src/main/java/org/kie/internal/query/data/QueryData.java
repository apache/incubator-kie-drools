package org.kie.internal.query.data;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import org.kie.internal.query.QueryContext;

@XmlType
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryData extends QueryParameters { 

    @XmlElement
    private QueryContext queryContext = new QueryContext();
    
    public QueryData() { 
        // JAXB constructor
    }
    
    public QueryData(QueryData queryData) { 
       super((QueryParameters) queryData); 
       this.queryContext = new QueryContext(queryData.getQueryContext());
    }
    
    public QueryContext getQueryContext() {
        return queryContext;
    }

    public void setQueryContext( QueryContext queryContext ) {
        this.queryContext = queryContext;
    }

    public void clear() { 
        super.clear();
        this.queryContext.clear();
    }

}
