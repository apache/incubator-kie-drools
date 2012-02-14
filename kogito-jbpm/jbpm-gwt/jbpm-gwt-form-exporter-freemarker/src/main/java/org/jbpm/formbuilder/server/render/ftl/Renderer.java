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
package org.jbpm.formbuilder.server.render.ftl;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.Map;

import org.jbpm.formapi.server.render.RendererException;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class Renderer implements org.jbpm.formapi.server.render.Renderer {

    @Override
    public Object render(URL url, Map<String, Object> inputData) throws RendererException {
        try {
            //return FileUtils.readFileToString(new File(url.getFile()));
            Configuration cfg = new Configuration();
            cfg.setObjectWrapper(new DefaultObjectWrapper());
            cfg.setTemplateUpdateDelay(0);
            String name = "formBuilderRender";
            StringWriter out = new StringWriter();
            Template temp = new Template(name, new InputStreamReader(url.openStream()), cfg);
            temp.process(inputData, out);
            return out.toString();
        } catch (IOException e) {
            throw new RendererException("I/O problem rendering " + url, e);
        } catch (TemplateException e) {
            throw new RendererException("Template problem rendering " + url, e);
        }
    }
}
