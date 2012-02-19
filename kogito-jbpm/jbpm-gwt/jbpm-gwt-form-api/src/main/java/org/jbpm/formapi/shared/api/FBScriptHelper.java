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
package org.jbpm.formapi.shared.api;

import com.google.gwt.user.client.ui.Widget;

/**
 * Helper to create visually attractive components for 
 * generating javascript editors
 */
public interface FBScriptHelper extends Mappable {

    /**
     * Transform any UI loaded content into a script implementation
     * @return a script
     */
    String asScriptContent();
    
    /**
     * Returns a UI component that implements visual contents
     * @return
     */
    Widget draw();
    
    /**
     * Returns the name of the UI script helper
     */
    String getName();
    
    /**
     * For initialization purposes, the represented script.
     * @param script
     */
    void setScript(FBScript script);
}
