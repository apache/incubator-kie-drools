/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.form.builder.services.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author salaboy
 */
@Entity
@XmlRootElement (name = "settings")
public class Settings implements Serializable {
    
    @Id
    @GeneratedValue()
    private Long id;
    @OneToMany(cascade= CascadeType.ALL, fetch= FetchType.EAGER)
    
    private List<SettingsEntry> entries = new ArrayList<SettingsEntry>();

    private String userId;
    
    public Settings() {
    }

    public Settings(String userId) {
        this.userId = userId;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<SettingsEntry> getEntries() {
        return entries;
    }
    
    public SettingsEntry getEntry(String key) {
        for(SettingsEntry entry : entries){
            if(entry.getKey().equals(key)){
                return entry;
            }
        }
        return null;
    }

    public void setEntries(List<SettingsEntry> entries) {
        this.entries = entries;
    }

    public void addEntry(SettingsEntry entry){
        this.entries.add(entry);
    }
    
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "Settings{" + "id=" + id + ", userId=" + userId + ", entries=" + entries +  '}';
    }

	public Map<String, Object> getDataMap() {
		Map<String, Object> dataMap = new HashMap<String, Object>();
		dataMap.put("org.jbpm.form.builder.services.model.Settings.userId", this.userId);
		dataMap.put("org.jbpm.form.builder.services.model.Settings.id", this.id == null ? null : String.valueOf(this.id));
		for (SettingsEntry entry : entries) {
			dataMap.put(entry.getKey(), entry.getValue());
			dataMap.put(entry.getKey() + "@org.jbpm.form.builder.services.model.SettingsEntry.id", 
					entry.getId() == null ? null : String.valueOf(entry.getId()));
		}
		return dataMap;
	}

	public void setDataMap(Map<String, Object> settingsDto) {
		Object objUserId = settingsDto.remove("org.jbpm.form.builder.services.model.Settings.userId");
    	this.userId = objUserId == null ? null : String.valueOf(objUserId);
    	Object objId = settingsDto.remove("org.jbpm.form.builder.services.model.Settings.id");
    	this.id = objId == null ? null : Long.valueOf(String.valueOf(objId));
    	this.entries.clear();
    	for (Map.Entry<String, Object> entry : settingsDto.entrySet()) {
    		if (entry.getKey().endsWith("@org.jbpm.form.builder.services.model.SettingsEntry.id")) {
    			continue;
    		}
    		String value = entry.getValue() == null ? null : String.valueOf(entry.getValue());
    		Object objEntryId = settingsDto.get(entry.getKey() + "@org.jbpm.form.builder.services.model.SettingsEntry.id"); 
    		Long entryId = objEntryId == null ? null : Long.valueOf(String.valueOf(objEntryId));
    		SettingsEntry settingsEntry = new SettingsEntry(entry.getKey(), value);
    		settingsEntry.setId(entryId);
    		entries.add(settingsEntry);
    	}
	}

    
    
    
    
}
