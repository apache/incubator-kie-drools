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

import java.util.Map;

import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.form.FormEncodingException;

import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class VideoRepresentation extends FormItemRepresentation {

	private String cssClassName;
	private String id;
	private String dataType;
	private String videoUrl;
	
	public VideoRepresentation() {
		super("video");
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

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}
	
    @Override
    public Map<String, Object> getDataMap() {
        Map<String, Object> data = super.getDataMap();
        data.put("videoUrl", this.videoUrl);
        data.put("cssClassName", this.cssClassName);
        data.put("dataType", this.dataType);
        data.put("id", this.id);
        return data;
    }
    
    @Override
    public void setDataMap(Map<String, Object> data) throws FormEncodingException {
    	super.setDataMap(data);    
    	this.videoUrl = (String) data.get("videoUrl");
    	this.cssClassName = (String) data.get("cssClassName");
    	this.dataType = (String) data.get("dataType");
    	this.id = (String) data.get("id");
    }
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof VideoRepresentation)) return false;
        VideoRepresentation other = (VideoRepresentation) obj;
        boolean equals = (this.videoUrl == null && other.videoUrl == null) || 
            (this.videoUrl != null && this.videoUrl.equals(other.videoUrl));
        if (!equals) return equals;
        equals = (this.cssClassName == null && other.cssClassName == null) || 
        	(this.cssClassName != null && this.cssClassName.equals(other.cssClassName));
        if (!equals) return equals;
        equals = (this.dataType == null && other.dataType == null) || (this.dataType != null && this.dataType.equals(other.dataType));
        if (!equals) return equals;
        equals = (this.id == null && other.id == null) || (this.id != null && this.id.equals(other.id));
        return equals;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        int aux = this.videoUrl == null ? 0 : this.videoUrl.hashCode();
        result = 37 * result + aux;
        aux = this.cssClassName == null ? 0 : this.cssClassName.hashCode();
        result = 37 * result + aux;
        aux = this.dataType == null ? 0 : this.dataType.hashCode();
        result = 37 * result + aux;
        aux = this.id == null ? 0 : this.id.hashCode();
        result = 37 * result + aux;
        return result;
    }
}
