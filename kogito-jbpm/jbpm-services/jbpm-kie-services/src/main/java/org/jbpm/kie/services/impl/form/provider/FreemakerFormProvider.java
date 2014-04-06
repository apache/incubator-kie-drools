package org.jbpm.kie.services.impl.form.provider;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Map;

import org.jbpm.kie.services.impl.form.FormProvider;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Template;

public abstract class FreemakerFormProvider implements FormProvider {

    protected String render(String name, InputStream src, Map<String, Object> renderContext) {
        
        String str = null;
        try {
            freemarker.template.Configuration cfg = new freemarker.template.Configuration();
            BeansWrapper defaultInstance = new BeansWrapper();
            defaultInstance.setSimpleMapWrapper(true);
            cfg.setObjectWrapper(defaultInstance);
            cfg.setTemplateUpdateDelay(0);
            Template temp = new Template(name, new InputStreamReader(src), cfg);
            StringWriter out = new StringWriter();
            temp.process(renderContext, out);
            out.flush();
            str = out.getBuffer().toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to process form template", e);
        }
        return str;
    }
}
