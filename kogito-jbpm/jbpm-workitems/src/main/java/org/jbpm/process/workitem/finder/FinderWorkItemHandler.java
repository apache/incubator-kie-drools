/**
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

package org.jbpm.process.workitem.finder;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.process.instance.WorkItemHandler;
import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemManager;

public class FinderWorkItemHandler implements WorkItemHandler {

//	private FileFinder finder;
	
	public FinderWorkItemHandler() {
//		finder = new FileFinder();
	}
	
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		String path = (String) workItem.getParameter("Path");
		String regex = (String) workItem.getParameter("Regex");
		Map<String, Object> options = new HashMap<String, Object>();
//		options.put(Finder.REGEX, regex);
//		File[] files = finder.find(new File(path), options);
		List<File> fileList = new ArrayList<File>();
//		for (File file: files) {
//			fileList.add(file);
//		}
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("Files", fileList);
		manager.completeWorkItem(workItem.getId(), results);
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// Do nothing, this work item cannot be aborted
	}

}
