/*
 * Copyright 2011 JBoss Inc 
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
package org.jbpm.formapi.shared.api.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.graph.GraphEntry;
import org.jbpm.formapi.shared.form.FormEncodingException;

import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class LineGraphRepresentation extends FormItemRepresentation {

    private List<List<String>> dataTable = new ArrayList<List<String>>();
    private List<Map.Entry<String, String>> dataStructure = new ArrayList<Map.Entry<String, String>>();
    private String graphTitle;
    private String graphXTitle;
    private String graphYTitle;
    
    public LineGraphRepresentation() {
        super("lineGraph");
    }

    public List<List<String>> getDataTable() {
        return dataTable;
    }

    public void setDataTable(List<List<String>> dataTable) {
        this.dataTable = dataTable;
    }

    public List<Map.Entry<String, String>> getDataStructure() {
        return dataStructure;
    }

    public boolean addColumn(String key, String value) {
        return dataStructure.add(new GraphEntry(key, value));
    }
    
    public boolean addTuple(int x, int y, Object obj) {
        List<String> list = new ArrayList<String>();
        list.add(String.valueOf(x));
        list.add(String.valueOf(y));
        list.add(String.valueOf(obj));
        return dataTable.add(list);
    }

    public void setDataStructure(List<Map.Entry<String, String>> dataStructure) {
        this.dataStructure = dataStructure;
    }



    public String getGraphTitle() {
        return graphTitle;
    }

    public void setGraphTitle(String graphTitle) {
        this.graphTitle = graphTitle;
    }

    public String getGraphXTitle() {
        return graphXTitle;
    }

    public void setGraphXTitle(String graphXTitle) {
        this.graphXTitle = graphXTitle;
    }

    public String getGraphYTitle() {
        return graphYTitle;
    }

    public void setGraphYTitle(String graphYTitle) {
        this.graphYTitle = graphYTitle;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void setDataMap(Map<String, Object> data) throws FormEncodingException {
    	super.setDataMap(data);
    	List<Object> dataTableMap = (List<Object>) data.get("dataTable");
    	this.dataTable.clear();
    	if (dataTableMap != null) {
	    	for (Object obj : dataTableMap) {
	    		List<String> dataLine = new ArrayList<String>();
	    		if (obj != null) {
	    			List<Object> dataLineMap = (List<Object>) obj;
	    			for (Object d : dataLineMap) {
	    				dataLine.add(String.valueOf(d));
	    			}
	    		}
	    		this.dataTable.add(dataLine);
	    	}
    	}
    	this.dataStructure.clear();
    	List<Object> dataStructureMap = (List<Object>) data.get("dataStructure");
    	if (dataStructureMap != null) {
    		Map<String, String> entries = new HashMap<String, String>(); 
    		for (Object obj : dataStructureMap) {
    			if (obj != null) {
    				Map<String, Object> struc = (Map<String, Object>) obj;
    				if (!struc.isEmpty()) {
    					Map.Entry<String, Object> entryMap = struc.entrySet().iterator().next();
    					entries.put(entryMap.getKey(), String.valueOf(entryMap.getValue()));
    					this.dataStructure.add(entries.entrySet().iterator().next());
    					entries.clear();
    				}
    			}
    		}
    	}
    	this.graphTitle = (String) data.get("graphTitle");
    	this.graphXTitle = (String) data.get("graphXTitle");
    	this.graphYTitle = (String) data.get("graphYTitle");
    }

    @Override
    public Map<String, Object> getDataMap() {
    	Map<String, Object> data = super.getDataMap();
    	
    	List<Object> dataTableMap = new ArrayList<Object>();
    	for (List<String> dataLine : this.dataTable) {
    		List<Object> dataLineMap = new ArrayList<Object>();
    		if (dataLine != null) {
    			for (String d : dataLine) {
    				dataLineMap.add(d);
    			}
    		}
    		dataTableMap.add(dataLineMap);
    	}
    	data.put("dataTable", dataTableMap);
    	List<Object> dataStructureMap = new ArrayList<Object>();
    	for (Map.Entry<String, String> entry : this.dataStructure) {
    		Map<String, Object> entryMap = new HashMap<String, Object>();
    		entryMap.put(entry.getKey(), entry.getValue());
    		dataStructureMap.add(entryMap);
    	}
    	data.put("dataStructure", dataStructureMap);
    	data.put("graphTitle", this.graphTitle);
    	data.put("graphXTitle", this.graphXTitle);
    	data.put("graphYTitle", this.graphYTitle);
    	
    	return data;
    }

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((dataStructure == null) ? 0 : dataStructure.hashCode());
		result = prime * result + ((dataTable == null) ? 0 : dataTable.hashCode());
		result = prime * result + ((graphTitle == null) ? 0 : graphTitle.hashCode());
		result = prime * result + ((graphXTitle == null) ? 0 : graphXTitle.hashCode());
		result = prime * result + ((graphYTitle == null) ? 0 : graphYTitle.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		LineGraphRepresentation other = (LineGraphRepresentation) obj;
		if (dataStructure == null) {
			if (other.dataStructure != null) return false;
		} else if (!dataStructure.equals(other.dataStructure)) return false;
		if (dataTable == null) {
			if (other.dataTable != null) return false;
		} else if (!dataTable.equals(other.dataTable)) return false;
		if (graphTitle == null) {
			if (other.graphTitle != null) return false;
		} else if (!graphTitle.equals(other.graphTitle)) return false;
		if (graphXTitle == null) {
			if (other.graphXTitle != null) return false;
		} else if (!graphXTitle.equals(other.graphXTitle)) return false;
		if (graphYTitle == null) {
			if (other.graphYTitle != null) return false;
		} else if (!graphYTitle.equals(other.graphYTitle)) return false;
		return true;
	}
	
	public int getOffsetWidth() {
		if (getWidth() != null) {
			String height = getWidth();
			String aux = height.replace("px", "");
			if (aux != null) {
				return Integer.valueOf(aux);
			}
		}
		return 0;
	}
	
	public int getOffsetHeight() {
		if (getHeight() != null) {
			String height = getHeight();
			String aux = height.replace("px", "");
			if (aux != null) {
				return Integer.valueOf(aux);
			}
		}
		return 0;
	}
}
