package org.drools.lang.descr;

public class FromDescr {

	private ColumnDescr column;
	private DeclarativeInvokerDescr dataSource;
	
	public ColumnDescr getColumn() {
		return column;
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
	
}
