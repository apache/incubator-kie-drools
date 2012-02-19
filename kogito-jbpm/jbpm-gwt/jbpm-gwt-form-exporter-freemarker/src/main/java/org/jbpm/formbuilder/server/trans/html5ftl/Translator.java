package org.jbpm.formbuilder.server.trans.html5ftl;

import org.jbpm.formapi.server.trans.ScriptingTranslator;
import org.jbpm.formapi.server.trans.TranslatorException;
import org.jbpm.formapi.server.trans.TranslatorFactory;
import org.jbpm.formapi.shared.api.FBScript;

public class Translator extends ScriptingTranslator  {

    private static final String LANG = "html5ftl";

    public Translator() {
        super(LANG, "/langs/html5ftl/");
    }

    public String toServerScript(FBScript script) throws TranslatorException {
        if (isValidScript(script)) {
            return asFtlScript(script);
        } else {
            throw new TranslatorException(script.getType() + " is not a supported language");
        }
    }
    
    public String asFtlVar(String varName) {
        return (varName == null) ? "" : "${" + varName + "}";
    }

    private String asFtlScript(FBScript script) {
        StringBuilder builder = new StringBuilder();
        if (script.getContent() != null && !"".equals(script.getContent())) {
            builder.append(script.getContent());
        } else if (script.getSrc() != null && !"".equals(script.getSrc())) {
            builder.append("<#include '").append(script.getSrc()).append("'>\n");
        }
        return builder.toString();
    }

    private boolean isValidScript(FBScript script) {
        return script != null && script.getType() != null && 
        	(script.getType().contains("ftl") || script.getType().contains("freemarker"));
    }
    
    public boolean isClientScript(FBScript script) {
        return TranslatorFactory.getInstance().isClientSide(script.getType());
    }
}
