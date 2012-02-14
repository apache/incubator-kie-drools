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
package org.jbpm.formbuilder.server.trans.xulphp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jbpm.formapi.server.trans.ScriptingTranslator;

public class Translator extends ScriptingTranslator {

    private static final String LANG = "xulphp";

    public Translator() {
        super(LANG, "/langs/xulphp/");
    }
    
    public String toXulEscapedHtml(String html) {
        Matcher matcher1 = Pattern.compile("<([a-zA-Z\\n\\r]+?) (.+?)>", Pattern.MULTILINE).matcher(html);
        String retval = matcher1.replaceAll("<html\\:$1 xmlns:html=\"http://www.w3.org/1999/xhtml\" $2>");
        Matcher matcher2 = Pattern.compile("<([a-zA-Z\\n\\r]+?)(?m)>", Pattern.MULTILINE).matcher(retval);
        retval = matcher2.replaceAll("<html\\:$1 xmlns:html=\"http://www.w3.org/1999/xhtml\">");
        Matcher matcher3 = Pattern.compile("</([a-zA-Z\\n\\r]+?)>", Pattern.MULTILINE).matcher(retval);
        retval = matcher3.replaceAll("</html:$1>");
        return retval;
    }
}
