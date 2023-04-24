package org.drools.drlonyaml.todrl;

import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

public class YAMLtoDrlDumper {
    public static final Configuration CONFIGURATION = config();
    
    private static Configuration config() {
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_32); // this should ensure check of breaking changes on dependency update
        cfg.setClassForTemplateLoading(YAMLtoDrlDumper.class, "/");
        // Recommended settings for new projects:
        cfg.setDefaultEncoding("UTF-8");
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        cfg.setLogTemplateExceptions(false);
        cfg.setWrapUncheckedExceptions(true);
        cfg.setFallbackOnNullLoopVariable(false);
        return cfg;
    }
    
    public static String dumpDRL(org.drools.drlonyaml.model.Package drl) throws Exception {
        Template temp = CONFIGURATION.getTemplate("drl.ftl");

        Writer out = new StringWriter();
        temp.process(drl, out);
        String result = out.toString();
        out.close();
        return result;
    }
}
