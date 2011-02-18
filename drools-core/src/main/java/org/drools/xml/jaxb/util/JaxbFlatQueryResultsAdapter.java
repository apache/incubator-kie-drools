/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.xml.jaxb.util;

import java.util.ArrayList;
import java.util.Arrays;

import javax.xml.bind.annotation.adapters.XmlAdapter;


public class JaxbFlatQueryResultsAdapter extends XmlAdapter<Object[], ArrayList<ArrayList<Object>>> {

	@Override
	public Object[] marshal(ArrayList<ArrayList<Object>> value) throws Exception {
		if (value == null || value.isEmpty()) {
			return new Object[0];
		}
		Object[] ret = new Object[value.size()];
		int i = 0;
		for (ArrayList<Object> list : value) {
			int j = 0;
			Object[] sublist = new Object[list.size()];
			for (Object object : list) {
				sublist[j++] = object;
			}
			ret[i++] = sublist;
		}
		return ret;
	}

	@Override
	public ArrayList<ArrayList<Object>> unmarshal(Object[] value) throws Exception {
		ArrayList<ArrayList<Object>> ret = new ArrayList<ArrayList<Object>>();
		for( Object o : value ) {
			ArrayList<Object> subList;
			if (o instanceof Object[]) {
				Object[] list = (Object[]) o;
				subList = new ArrayList<Object>(list.length);
				for (Object obj : list) {
					subList.add(obj);
				}
			} else {
				subList = new ArrayList<Object>(Arrays.asList(new Object[] {o}));
			}
			ret.add(subList);
		}
		return ret;
	}

}
