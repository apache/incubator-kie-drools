package org.jbpm.kie.services.impl.form.provider;


import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import org.jbpm.kie.services.impl.form.FormProvider;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Map;

public abstract class FreemakerFormProvider implements FormProvider {

    protected String render(String name, InputStream src, Map<String, Object> renderContext) {

        String str = null;
        try {
            freemarker.template.Configuration cfg = new freemarker.template.Configuration();
            cfg.setObjectWrapper(new DefaultObjectWrapper());
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
