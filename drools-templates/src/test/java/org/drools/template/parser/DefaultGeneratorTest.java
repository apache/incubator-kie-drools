package org.drools.template.parser;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DefaultGeneratorTest {
	private DefaultGenerator g;

    @Before
    public void setUp() throws Exception {
        Map<String, RuleTemplate> t = new HashMap<String, RuleTemplate>();
        TemplateContainer tc = new TemplateContainer() {

			public Column getColumn(String name) {
				return null;
			}

			public Column[] getColumns() {
				return null;
			}

			public String getHeader() {
				return null;
			}

			public Map<String, RuleTemplate> getTemplates() {
				return null;
			}
        	
        };
        RuleTemplate rt1 = new RuleTemplate("rt1", tc);
        rt1.setContents("Test template 1");
        RuleTemplate rt2 = new RuleTemplate("rt2", tc);
        rt2.setContents("Test template 2");
        RuleTemplate rt3 = new RuleTemplate("rt3", tc);
        rt3.addColumn("col1");
        rt3.addColumn("col2");
        rt3.setContents("@{row.rowNumber} @{col1} @{col2}");
        t.put("rt1", rt1);
        t.put("rt2", rt2);
        t.put("rt3", rt3);
        g = new DefaultGenerator(t);
    }
    
    @Test
    public void testSelectTemplate() {
		g.generate("rt2", new Row());
		String drl = g.getDrl();
		assertEquals("Test template 2\n\n", drl);
	}
	
    @Test
    public void testGenerate() {
		g.generate("rt2", new Row());
		g.generate("rt1", new Row());
		String drl = g.getDrl();
		assertEquals("Test template 2\n\nTest template 1\n\n", drl);
	}
	
    @Test
    public void testAddColumns() {
		Column[] columns = {new StringColumn("col1"), new StringColumn("col2")};
		Row r = new Row(1, columns);
		r.getCell(0).setValue("value1");
		r.getCell(1).setValue("value2");
//		Row r = new Row(1);
//		r.addCell(new StringCell(r, new StringColumn("col1"), "value1"));
//		r.addCell(new StringCell(r, new StringColumn("col2"), "value2"));
		g.generate("rt3", r);
		String drl = g.getDrl();
		assertEquals("1 value1 value2\n\n", drl);
	}

}
