package org.drools.decisiontable.parser;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

public class DefaultGeneratorTest extends TestCase {
	private DefaultGenerator g;

    protected void setUp() throws Exception {
        Map t = new HashMap();
        RuleTemplate rt1 = new RuleTemplate("rt1");
        rt1.setContents("Test template 1");
        RuleTemplate rt2 = new RuleTemplate("rt2");
        rt2.setContents("Test template 2");
        RuleTemplate rt3 = new RuleTemplate("rt3");
        rt3.addColumn("col1");
        rt3.addColumn("col2");
        rt3.setContents("@{row.rowNumber} @{col1} @{col2}");
        t.put("rt1", rt1);
        t.put("rt2", rt2);
        t.put("rt3", rt3);
        g = new DefaultGenerator(t);
    }
    
	public void testSelectTemplate() {
		g.generate("rt2", new Row());
		String drl = g.getDrl();
		assertEquals("Test template 2\n\n", drl);
	}
	
	public void testGenerate() {
		g.generate("rt2", new Row());
		g.generate("rt1", new Row());
		String drl = g.getDrl();
		assertEquals("Test template 2\n\nTest template 1\n\n", drl);
	}
	
	public void testAddColumns() {
		Column[] columns = {new Column("col1"), new Column("col2")};
		Row r = new Row(1, columns);
		r.getCell(0).setValue("value1");
		r.getCell(1).setValue("value2");
//		r.addCell(new Cell(r, new Column("col1"), "value1"));
//		r.addCell(new Cell(r, new Column("col2"), "value2"));
		g.generate("rt3", r);
		String drl = g.getDrl();
		assertEquals("1 value1 value2\n\n", drl);
	}

}
