package org.drools.brms.modeldriven;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import junit.framework.TestCase;

import org.drools.brms.server.util.DataEnumLoader;
import org.mvel.MVEL;

public class DataEnumLoaderTest extends TestCase {

	public void testEnumGeneration() throws Exception {
		Object result = MVEL.eval("[2, 3, 4, ]", new HashMap());
		assertTrue(result instanceof List);
		List l = (List) result;
		assertEquals(3, l.size());

		result = MVEL.eval("['Person.age' : [2, 3], 'Person.name' : ['qqq', 'ccc']]", new HashMap());

		DataEnumLoader loader = new DataEnumLoader(readLines().toString());


		Map enumeration = (Map) loader.getData();
		assertEquals(loader.getErrors().toString(), 0, loader.getErrors().size());
		assertEquals(3, enumeration.size());

		List list = (List) enumeration.get("Person.age");
		assertEquals(4, list.size());
		assertEquals("1", list.get(0));
		assertEquals("2", list.get(1));

		list = (List) enumeration.get("Person.rating");
		assertEquals(2, list.size());
		assertEquals("High", list.get(0));
		assertEquals("Low", list.get(1));


		loader = new DataEnumLoader("");
		assertEquals(1, loader.getErrors().size());

		loader = new DataEnumLoader("goober poo error");
		assertEquals(0, loader.getData().size());
		assertFalse(loader.getErrors().size() == 0);


	}


	private StringBuffer readLines() throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("Some.enumeration")));
		String line = "";
		StringBuffer buf = new StringBuffer();
		while ((line = r.readLine()) != null) {
			buf.append(line); buf.append('\n');
		}
		return buf;
	}


}
