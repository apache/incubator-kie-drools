package org.drools.brms.modeldriven;

import java.util.ArrayList;
import java.util.List;

public class SampleDataSource {

	public static List getData() {
		return new ArrayList() {{
			add("Hello");
			add("World");
		}};
	}

}
