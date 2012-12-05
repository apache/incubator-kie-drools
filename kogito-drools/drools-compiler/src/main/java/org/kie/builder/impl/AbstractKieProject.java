package org.kie.builder.impl;

import java.util.HashMap;
import java.util.Map;

import org.drools.kproject.models.KieBaseModelImpl;
import org.kie.builder.KieBaseModel;

public abstract class AbstractKieProject implements KieProject {

    protected Map<String, KieBaseModel> kBaseModels       = new HashMap<String, KieBaseModel>();

    public Messages verify() {
        Messages messages = new Messages();
        verify(messages);
        return messages;
    }

    public void verify(Messages messages) {

        for ( KieBaseModel model : kBaseModels.values() ) {
            AbstractKieModule.createKieBase( (KieBaseModelImpl) model,
                    this,
                    messages );
        }
    }

    public KieBaseModel getKieBaseModel(String kBaseName) {
        return kBaseModels.get( kBaseName );
    }
}
