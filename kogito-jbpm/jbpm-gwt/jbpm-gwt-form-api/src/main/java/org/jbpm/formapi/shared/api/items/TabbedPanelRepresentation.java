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
import org.jbpm.formapi.shared.form.FormEncodingException;
import org.jbpm.formapi.shared.form.FormEncodingFactory;

import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class TabbedPanelRepresentation extends FormItemRepresentation {

    public class IndexedString {
        private final int index;
        private final String string;
        
        public IndexedString(int index, String string) {
            super();
            this.index = index;
            this.string = string;
        }
        
        public int getIndex() {
            return index;
        }
        
        public String getString() {
            return string;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null) return false;
            if (!(obj instanceof IndexedString)) return false;
            IndexedString other = (IndexedString) obj;
            boolean equals = this.index == other.index;
            if (!equals) return equals;
            equals = (this.string == null && other.string == null) || (this.string != null && this.string.equals(other.string));
            return equals;
        }
        
        @Override
        public int hashCode() {
            int result = super.hashCode();
            result = 37 * result + this.index;
            int aux = this.string == null ? 0 : this.string.hashCode();
            result = 37 * result + aux;
            return result;
        }
    }
    
    private List<IndexedString> tabTitles = new ArrayList<IndexedString>();
    private Map<IndexedString, FormItemRepresentation> tabContents = new HashMap<IndexedString, FormItemRepresentation>();
    private String cssClassName;
    private String id;
    private String tabWidth;
    
    public TabbedPanelRepresentation() {
        super("tabbedPanel");
    }

    public void putTab(int index, String tabTitle, FormItemRepresentation tabContent) {
        if (index >= tabTitles.size()) {
            tabTitles.add(new IndexedString(index, tabTitle));
        } else {
            IndexedString myTitle = new IndexedString(index, tabTitle);
            IndexedString prevTitle = tabTitles.get(index);
            if (prevTitle == null) {
                tabTitles.set(index, myTitle);
            } else {
                List<IndexedString> nextValues = tabTitles.subList(index, tabTitles.size());
                tabTitles.removeAll(nextValues);
                tabTitles.add(myTitle);
                tabTitles.addAll(nextValues);
            }
        }
        tabContents.put(new IndexedString(index, tabTitle), tabContent);
    }

    public List<IndexedString> getTabTitles() {
        return tabTitles;
    }

    public void setTabTitles(List<IndexedString> tabTitles) {
        this.tabTitles = tabTitles;
    }

    public Map<IndexedString, FormItemRepresentation> getTabContents() {
        return tabContents;
    }

    public void setTabContents(Map<IndexedString, FormItemRepresentation> tabContents) {
        this.tabContents = tabContents;
    }

    public String getCssClassName() {
        return cssClassName;
    }

    public void setCssClassName(String cssClassName) {
        this.cssClassName = cssClassName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTabWidth() {
        return tabWidth;
    }

    public void setTabWidth(String tabWidth) {
        this.tabWidth = tabWidth;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setDataMap(Map<String, Object> data) throws FormEncodingException {
        super.setDataMap(data);
        this.cssClassName = (String) data.get("cssClassName");
        this.id = (String) data.get("id");
        this.tabWidth = (String) data.get("tabWidth");
        this.tabTitles.clear();
        List<Object> tabTitles = (List<Object>) data.get("tabTitles");
        List<Object> tabContents = (List<Object>) data.get("tabContents");
        for (Object tTitle : tabTitles) {
            Map<String, Object> subData = (Map<String, Object>) tTitle;
            Integer index = Integer.valueOf(String.valueOf(subData.get("index")));
            String string = (String) subData.get("string");
            this.tabTitles.add(new IndexedString(index, string));
        }
        this.tabContents.clear();
        for (Object tContent : tabContents) {
            Map<String, Object> subData = (Map<String, Object>) tContent;
            Integer index = Integer.valueOf(String.valueOf(subData.get("tabPanelIndex")));
            String string = (String) subData.get("tabPanelTitle");
            FormItemRepresentation subRep = (FormItemRepresentation) FormEncodingFactory.getDecoder().decode(subData);
            this.tabContents.put(new IndexedString(index, string), subRep);
        }
    }
    
    @Override
    public Map<String, Object> getDataMap() {
        Map<String, Object> data = super.getDataMap();
        data.put("cssClassName", this.cssClassName);
        data.put("id", this.id);
        data.put("tabWidth", this.tabWidth);
        List<Object> tabTitlesMap = new ArrayList<Object>();
        for (IndexedString inStr : this.tabTitles) {
            Map<String, Object> inStrMap = new HashMap<String, Object>();
            inStrMap.put("index", inStr.getIndex());
            inStrMap.put("string", inStr.getString());
            tabTitlesMap.add(inStrMap);
        }
        data.put("tabTitles", tabTitlesMap);
        List<Object> tabContentsMap = new ArrayList<Object>();
        for (Map.Entry<IndexedString, FormItemRepresentation> entry : this.tabContents.entrySet()) {
            Map<String, Object> subData = entry.getValue() == null ? new HashMap<String, Object>() : entry.getValue().getDataMap();
            subData.put("tabPanelIndex", entry.getKey().getIndex());
            subData.put("tabPanelTitle", entry.getKey().getString());
            tabContentsMap.add(subData);
        }
        data.put("tabContents", tabContentsMap);
        return data;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof TabbedPanelRepresentation)) return false;
        TabbedPanelRepresentation other = (TabbedPanelRepresentation) obj;
        boolean equals = (this.cssClassName == null && other.cssClassName == null) || 
            (this.cssClassName != null && this.cssClassName.equals(other.cssClassName));
        if (!equals) return equals;
        equals = (this.id == null && other.id == null) || (this.id != null && this.id.equals(other.id));
        if (!equals) return equals;
        equals = (this.tabWidth == null && other.tabWidth == null) || (this.tabWidth != null && this.tabWidth.equals(other.tabWidth));
        if (!equals) return equals;
        equals = (this.tabTitles == null && other.tabTitles == null) || (this.tabTitles != null && this.tabTitles.equals(other.tabTitles));
        if (!equals) return equals;
        equals = (this.tabContents == null && other.tabContents == null) || 
            (this.tabContents != null && this.tabContents.entrySet().equals(other.tabContents.entrySet()));
        return equals;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        int aux = this.cssClassName == null ? 0 : this.cssClassName.hashCode();
        result = 37 * result + aux;
        aux = this.id == null ? 0 : this.id.hashCode();
        result = 37 * result + aux;
        aux = this.tabTitles == null ? 0 : this.tabTitles.hashCode();
        result = 37 * result + aux;
        aux = this.tabContents == null ? 0 : this.tabContents.hashCode();
        result = 37 * result + aux;
        return result;
    }
    
}
