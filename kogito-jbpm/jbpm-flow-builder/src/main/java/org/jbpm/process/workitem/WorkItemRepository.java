package org.jbpm.process.workitem;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.core.process.core.ParameterDefinition;
import org.drools.core.process.core.datatype.DataType;
import org.drools.core.process.core.impl.ParameterDefinitionImpl;
import org.drools.core.util.ConfFileUtils;
import org.jbpm.flow.util.MVELSafeHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkItemRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(WorkItemRepository.class);

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
				workDefinition.setCategory((String) workDefinitionMap.get("category"));
				workDefinition.setPath((String) workDefinitionMap.get("path"));
				workDefinition.setFile((String) workDefinitionMap.get("file"));
				workDefinition.setDocumentation((String) workDefinitionMap.get("documentation"));
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
			workDefinitions.addAll(getWorkDefinitionsMap(directory, s));
		}
		return workDefinitions;
	}

	private static String[] getDirectories(String path) {
		String content = null;
		try {
			content = ConfFileUtils.URLContentsToString(
				new URL(path + "/index.conf"));
		} catch (Exception e) {
			// Do nothing
		}
		if (content == null) {
			return new String[0];
		}
		return content.split("\n");
	}

	private static List<Map<String, Object>> getWorkDefinitionsMap(String parentPath, String file) {
		String path = parentPath + "/" + file + "/" + file + ".wid";
		String content = null;
		try {
			content = ConfFileUtils.URLContentsToString(new URL(path));
		} catch (Exception e) {
			// Do nothing
		}
		if (content == null) {
			return new ArrayList<Map<String, Object>>();
		}
		try {
			List<Map<String, Object>> result = (List<Map<String, Object>>) MVELSafeHelper.getEvaluator().eval(content, new HashMap());
			for (Map<String, Object> wid: result) {
				wid.put("path", parentPath + "/" + file);
				wid.put("file", file + ".wid");
			}
			return result;
		} catch (Throwable t) {
		    logger.error("Error occured while loading work definitions " + path, t);
			throw new RuntimeException("Could not parse work definitions " + path + ": " + t.getMessage());
		}
	}

}
