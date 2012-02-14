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
package org.jbpm.formbuilder.client.bus;

import com.google.gwt.event.shared.GwtEvent;

public class PreviewFormResponseEvent extends GwtEvent<PreviewFormResponseHandler> {

    public static final Type<PreviewFormResponseHandler> TYPE = new Type<PreviewFormResponseHandler>();
    
    private final String url;
    private final String previewType;
    
    public PreviewFormResponseEvent(String url, String previewType) {
        super();
        this.url = url;
        this.previewType = previewType;
    }

    public String getUrl() {
        return url;
    }
    
    public String getPreviewType() {
        return previewType;
    }
    
    @Override
    public Type<PreviewFormResponseHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(PreviewFormResponseHandler handler) {
        handler.onServerResponse(this);
    }

}
