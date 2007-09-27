package org.drools.brms.modeldriven;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SampleDataSource2 {

	public Map loadData() {
		Map data = new HashMap();

		List d = new ArrayList();
		d.add("hey");
		d.add("ho");
		data.put("whee", d);

		return data;
	}

}
