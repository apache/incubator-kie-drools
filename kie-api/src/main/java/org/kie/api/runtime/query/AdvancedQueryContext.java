package org.kie.api.runtime.query;

/**
 * 
 * Extension class to the basic ordering and pagination queries which use <code>QueryContext</code>.  
 * The AdvancedQueryContext is intended for use wherever advanced query functionality provided by the
 * KIE Query Service which provides advanced search capabilities that are based on Dashbuilder DataSets.
 * 
 */
public class AdvancedQueryContext extends QueryContext {

    private String orderByClause;

    public AdvancedQueryContext() {
        super();
    }

    public AdvancedQueryContext(QueryContext queryContext) {
        super(queryContext);
    }

    public AdvancedQueryContext(QueryContext queryContext, String orderByClause) {
        super(queryContext);
        this.orderByClause = orderByClause;
    }

    public AdvancedQueryContext(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * @return  the SQL Order By clause
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * Set the ORDER BY clause for advanced query.  For instance:
     * 
     * SELECT * FROM PROCESSINSTANCELOG 
     * ORDER BY PROCESSID ASC, PROCESSINSTANCEID DESC
     * 
     * has orderByClause of "PROCESSID ASC, PROCESSINSTANCEID DESC"
     * 
     * @param orderByClause the SQL Order By clause to set
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

}
