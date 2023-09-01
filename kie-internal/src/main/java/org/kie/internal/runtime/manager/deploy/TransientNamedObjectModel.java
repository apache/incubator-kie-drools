package org.kie.internal.runtime.manager.deploy;

import org.kie.internal.runtime.conf.NamedObjectModel;

public class TransientNamedObjectModel extends NamedObjectModel {
    private static final long serialVersionUID = -8210248739969022897L;

    public TransientNamedObjectModel() {
        super();
    }

    public TransientNamedObjectModel(String name, String classname, Object... parameters) {
        super(name, classname, parameters);
    }

    public TransientNamedObjectModel(String resolver, String name, String classname, Object... parameters) {
        super(resolver, name, classname, parameters);
    }

}
