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
package org.jbpm.formbuilder.client.command;

import com.google.gwt.user.client.Window;
import com.gwtent.reflection.client.Reflectable;

/**
 * Handles the preview action for XSL
 */
@Reflectable
public class PreviewFormAsGwtCommand extends PreviewFormCommand {

    private static final String LANG = "gwt";
    
    public PreviewFormAsGwtCommand() {
        super(LANG);
    }
    
    @Override
    protected void refreshPopup(String url) {
        String queryString = Window.Location.getQueryString();
        if (queryString != null && queryString.contains("gwt.codesvr")) {
            int start = queryString.indexOf("gwt.codesvr");
            int end = queryString.substring(start).indexOf("&") < 0 ? queryString.length() : queryString.substring(start).indexOf("&");
            if (url.contains("?")) {
                url += "&";
            } else {
                url += "?";
            }
            url += queryString.substring(start, end);
        }
        super.refreshPopup(url);
    }
}
