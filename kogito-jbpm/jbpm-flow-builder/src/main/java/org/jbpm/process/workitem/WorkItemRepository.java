package org.jbpm.process.workitem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.core.util.ConfFileUtils;
import org.drools.process.core.ParameterDefinition;
import org.drools.process.core.datatype.DataType;
import org.drools.process.core.impl.ParameterDefinitionImpl;
import org.mvel2.MVEL;

public class WorkItemRepository {

	public static Map<String, WorkDefinitionImpl> getWorkDefinitions(String path) {
		Map<String, WorkDefinitionImpl> workDefinitions = new HashMap<String, WorkDefinitionImpl>();
		List<Map<String, Object>> workDefinitionsMaps = getAllWorkDefinitionsMap(path);
		for (Map<String, Object> workDefinitionMap : workDefinitionsMaps) {
			if (workDefinitionMap != null) {
				WorkDefinitionImpl workDefinition = new WorkDefinitionImpl();
				workDefinition.setName((String) workDefinitionMap.get("name"));
				workDefinition.setDisplayName((String) workDefinitionMap.get("displayName"));
				workDefinition.setIcon((String) workDefinitionMap.get("icon"));
				workDefinition.setCustomEditor((String) workDefinitionMap.get("customEditor"));
				Set<ParameterDefinition> parameters = new HashSet<ParameterDefinition>();
				Map<String, DataType> parameterMap = (Map<String, DataType>) workDefinitionMap.get("parameters");
				if (parameterMap != null) {
					for (Map.Entry<String, DataType> entry : parameterMap.entrySet()) {
						parameters.add(new ParameterDefinitionImpl(entry.getKey(), entry.getValue()));
					}
				}
				workDefinition.setParameters(parameters);
				Set<ParameterDefinition> results = new HashSet<ParameterDefinition>();
				Map<String, DataType> resultMap = (Map<String, DataType>) workDefinitionMap.get("results");
				if (resultMap != null) {
					for (Map.Entry<String, DataType> entry : resultMap.entrySet()) {
						results.add(new ParameterDefinitionImpl(entry.getKey(),	entry.getValue()));
					}
				}
				workDefinition.setResults(results);
				workDefinition.setDefaultHandler((String) workDefinitionMap.get("defaultHandler"));
				workDefinition.setDependencies(((List<String>) workDefinitionMap.get("dependencies")).toArray(new String[0]));
				workDefinitions.put(workDefinition.getName(), workDefinition);
			}
		}
		return workDefinitions;
	}

	private static List<Map<String, Object>> getAllWorkDefinitionsMap(String directory) {
		List<Map<String, Object>> workDefinitions = new ArrayList<Map<String, Object>>();
		for (String s: getDirectories(directory)) {
			try {
				workDefinitions.addAll(getAllWorkDefinitionsMap(directory + "/" + s));
			} catch (Throwable t) {
				t.printStackTrace();
			}
			workDefinitions.addAll(getWorkDefinitionsMap(directory + "/" + s + "/" + s + ".conf"));
		}
		return workDefinitions;
	}

	private static String[] getDirectories(String path) {
		String content = ConfFileUtils.URLContentsToString(
			ConfFileUtils.getURL(path + "/index.conf", null, null));
		if (content == null) {
			return new String[0];
		}
		return content.split(System.getProperty("line.separator"));
	}

	private static List<Map<String, Object>> getWorkDefinitionsMap(String path) {
		String content = ConfFileUtils.URLContentsToString(
			ConfFileUtils.getURL(path, null, null));
		if (content == null) {
			return new ArrayList<Map<String, Object>>();
		}
		try {
			return (List<Map<String, Object>>) MVEL.eval(content, new HashMap());
		} catch (Throwable t) {
			System.err.println("Error occured while loading work definitions " + path);
			t.printStackTrace();
			throw new RuntimeException("Could not parse work definitions " + path + ": " + t.getMessage());
		}
	}

}
