package org.drools.lang.descr;

import java.util.List;

public class FromDescr extends BaseDescr
    implements
    ConditionalElementDescr { 
	private ColumnDescr column;
	private DeclarativeInvokerDescr dataSource;
	
	FromDescr() {
		//protected so only factory can create
	}
	
	public int getLine() {
		return column.getLine();
	}
	public void setColumn(ColumnDescr column) {
		this.column = column;
	}
	public DeclarativeInvokerDescr getDataSource() {
		return dataSource;
	}
	public void setDataSource(DeclarativeInvokerDescr dataSource) {
		this.dataSource = dataSource;
	}
	
	public ColumnDescr getReturnedColumn() {
		return column;
	}

    public void addDescr(BaseDescr baseDescr) {
        //
    }

    public List getDescrs() {
        // TODO Auto-generated method stub
        return null;
    }
	
   
}
