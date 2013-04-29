package org.jbpm.runtime.manager.impl;

import org.kie.api.KieBase;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.KieSessionConfiguration;
import org.kie.internal.runtime.manager.Mapper;
import org.kie.internal.runtime.manager.RegisterableItemsFactory;
import org.kie.internal.runtime.manager.RuntimeEnvironment;
import org.kie.internal.task.api.UserGroupCallback;

public class KModuleRuntimeEnvironment implements RuntimeEnvironment {

    @Override
    public KieBase getKieBase() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Environment getEnvironment() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public KieSessionConfiguration getConfiguration() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean usePersistence() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public RegisterableItemsFactory getRegisterableItemsFactory() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Mapper getMapper() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public UserGroupCallback getUserGroupCallback() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

}
