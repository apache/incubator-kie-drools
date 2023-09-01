package org.kie.internal.runtime.manager.deploy;

import javax.xml.bind.annotation.XmlTransient;

import org.kie.internal.runtime.conf.ObjectModel;

@XmlTransient
public class TransientObjectModel extends ObjectModel {
    private static final long serialVersionUID = -8210248739969022897L;

    public TransientObjectModel() {
        super();
    }

    public TransientObjectModel(String identifier, Object... parameters) {
        super(identifier, parameters);
    }

    public TransientObjectModel(String resolver, String identifier, Object... parameters) {
        super(resolver, identifier, parameters);
    }


}
