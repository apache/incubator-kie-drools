package org.drools.examples.sudoku;
/**
 * @author <a href="mailto:michael.frandsen@syngenio.de">Michael Frandsen</a>
 */
public class PossibleValue {
	
	private String value;
	private int column;
	private int row;
	private int zone;
	public PossibleValue() {
		super();
	}
	public PossibleValue(String value, int column, int row, int zone) {
		super();
		this.value = value;
		this.column = column;
		this.row = row;
		this.zone = zone;
	}
	public PossibleValue(String value, Field field) {
		super();
		this.value = value;
		this.column = field.getColumn();
		this.row = field.getRow();
		this.zone = field.getZone();
	}
	public int getColumn() {
		return column;
	}
	public void setColumn(int column) {
		this.column = column;
	}
	public int getRow() {
		return row;
	}
	public void setRow(int row) {
		this.row = row;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public int getZone() {
		return zone;
	}
	public void setZone(int zone) {
		this.zone = zone;
	}
}
