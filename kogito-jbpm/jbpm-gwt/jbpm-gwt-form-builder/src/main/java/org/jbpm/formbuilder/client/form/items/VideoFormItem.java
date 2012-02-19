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
package org.jbpm.formbuilder.client.form.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.client.FormBuilderException;
import org.jbpm.formapi.client.effect.FBFormEffect;
import org.jbpm.formapi.client.form.FBFormItem;
import org.jbpm.formapi.client.form.HasSourceReference;
import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.api.items.VideoRepresentation;
import org.jbpm.formbuilder.client.FormBuilderGlobals;
import org.jbpm.formbuilder.client.messages.I18NConstants;

import com.google.gwt.media.client.Video;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class VideoFormItem extends FBFormItem implements HasSourceReference {

	private final I18NConstants i18n = FormBuilderGlobals.getInstance().getI18n();
	
	private final Video video = Video.createIfSupported();
	private final Label notSupported = new Label(i18n.VideoNotSupported());
	
	private String cssClassName;
	private String id;
	private String dataType;
	private String videoUrl;
	
	public VideoFormItem() {
		this(new ArrayList<FBFormEffect>());
	}
	
	public VideoFormItem(List<FBFormEffect> formEffects) {
		super(formEffects);
		if (video == null) {
			add(notSupported);
		} else {
			add(video);
		}
		video.setWidth("300px");
		video.setHeight("200px");
		video.setControls(true);
		setWidth("300px");
		setHeight("200px");
	}

	@Override
	public Map<String, Object> getFormItemPropertiesMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("cssClassName", this.cssClassName);
        map.put("dataType", this.dataType);
        map.put("height", this.getHeight());
        map.put("width", this.getWidth());
        map.put("videoUrl", this.videoUrl);
        map.put("id", this.id);
		return map;
	}

	@Override
	public void saveValues(Map<String, Object> asPropertiesMap) {
        this.cssClassName = extractString(asPropertiesMap.get("cssClassName"));
        this.setHeight(extractString(asPropertiesMap.get("height")));
        this.setWidth(extractString(asPropertiesMap.get("width")));
        this.videoUrl = extractString(asPropertiesMap.get("videoUrl"));
        this.id = extractString(asPropertiesMap.get("id"));
        this.dataType = extractString(asPropertiesMap.get("dataType"));
        populate(this.video);
	}
	
	private void populate(Video video) {
		if (video != null) {
	        if (this.cssClassName != null) {
	        	video.setStyleName(this.cssClassName);
	        }
	        if (this.getHeight() != null) {
	        	video.setHeight(this.getHeight());
	        }
	        if (this.getWidth() != null) {
	        	video.setWidth(this.getWidth());
	        }
	        if (this.videoUrl != null && !"".equals(this.videoUrl)) {
	        	video.setSrc(this.videoUrl);
	        }
	        if (this.dataType != null) {
	        	video.getElement().setPropertyObject("type", this.dataType);
	        }
	        video.setControls(true);
		}
    }

	@Override
	public FormItemRepresentation getRepresentation() {
		VideoRepresentation rep = super.getRepresentation(new VideoRepresentation());
		rep.setVideoUrl(this.videoUrl);
		rep.setCssClassName(this.cssClassName);
		rep.setDataType(this.dataType);
		rep.setId(this.id);
		return rep;
	}
	
	@Override
    public void populate(FormItemRepresentation rep) throws FormBuilderException {
        if (!(rep instanceof VideoRepresentation)) {
            throw new FormBuilderException(i18n.RepNotOfType(rep.getClass().getName(), "VideoRepresentation"));
        }
        super.populate(rep);
        VideoRepresentation vrep = (VideoRepresentation) rep;
        this.videoUrl = vrep.getVideoUrl();
        this.cssClassName = vrep.getCssClassName();
        this.id = vrep.getId();
        this.dataType = vrep.getDataType();

        populate(this.video);
    }

	@Override
	public FBFormItem cloneItem() {
		VideoFormItem clone = super.cloneItem(new VideoFormItem());
        clone.setHeight(this.getHeight());
        clone.setWidth(this.getWidth());
        clone.videoUrl = this.videoUrl;
        clone.cssClassName = this.cssClassName;
        clone.dataType = this.dataType;
        clone.id = this.id;
        clone.populate(clone.video);
		return clone;
	}

	@Override
	public Widget cloneDisplay(Map<String, Object> formData) {
		Video v = Video.createIfSupported();
		if (v == null) {
			return new Label(notSupported.getText());
		}
        populate(v);
        Object input = getInputValue(formData);
        if (v != null && input != null) {
        	String url = input.toString();
			v.setSrc(url);
        	if (url.endsWith(".ogv")) {
        		v.getElement().setPropertyString("type", "video/ogg");
        	} else if (url.endsWith(".mpeg") || url.endsWith(".mpg")) {
        		v.getElement().setPropertyString("type", "video/mpeg");
        	} else if (url.endsWith(".avi")) {
        		v.getElement().setPropertyString("type", "video/avi");
        	}
        }
        super.populateActions(v.getElement());
        return v;
	}

	@Override
	public void setSourceReference(String sourceReference) {
		this.videoUrl = sourceReference;
		if (video != null) {
			this.video.setSrc(sourceReference);
		}
	}

	@Override
	public String getSourceReference() {
		return this.videoUrl;
	}

	@Override
	public List<String> getAllowedTypes() {
		ArrayList<String> retval = new ArrayList<String>();
        retval.add("mpeg");
        retval.add("mpg");
        retval.add("avi");
        retval.add("ogv");
        return retval;
	}
}
