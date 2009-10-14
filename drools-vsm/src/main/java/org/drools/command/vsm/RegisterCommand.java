package org.drools.command.vsm;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.vsm.ServiceManagerData;

public class RegisterCommand
    implements
    GenericCommand<Void> {

    private String identifier;
    private String instanceId;
    private int    type;

    public RegisterCommand(String identifier,
                           String instanceId,
                           int type) {
        this.identifier = identifier;
        this.instanceId = instanceId;
        this.type = type;
    }

    public Void execute(Context context) {
        ServiceManagerData data = (ServiceManagerData) context.get( ServiceManagerData.SERVICE_MANAGER_DATA );

        data.getRoot().set( identifier,
                            type + ":" + instanceId );

        return null;
    }

}
