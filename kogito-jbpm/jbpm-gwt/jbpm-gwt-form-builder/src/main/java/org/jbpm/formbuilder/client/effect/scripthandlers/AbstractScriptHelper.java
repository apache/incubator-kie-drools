package org.jbpm.formbuilder.client.effect.scripthandlers;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.formapi.shared.api.FBScript;
import org.jbpm.formapi.shared.api.FBScriptHelper;

public abstract class AbstractScriptHelper implements FBScriptHelper {

    @Override
    public void setScript(FBScript script) {
        List<FBScriptHelper> helpers = script.getHelpers();
        if (helpers == null) {
            helpers = new ArrayList<FBScriptHelper>();
        }
        if (!helpers.contains(this)) {
            helpers.add(this);
        }
        script.setHelpers(helpers);
    }
}
