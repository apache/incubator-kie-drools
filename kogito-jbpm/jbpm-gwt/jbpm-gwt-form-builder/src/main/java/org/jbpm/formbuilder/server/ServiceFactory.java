package org.jbpm.formbuilder.server;

import org.jbpm.formbuilder.server.file.FileService;
import org.jbpm.formbuilder.shared.form.FormDefinitionService;
import org.jbpm.formbuilder.shared.menu.MenuService;
import org.jbpm.formbuilder.shared.task.TaskDefinitionService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

public class ServiceFactory implements BeanFactoryAware {

    private static final ServiceFactory INSTANCE = new ServiceFactory();
    
    public static ServiceFactory getInstance() {
        return INSTANCE;
    }
    
    private ServiceFactory() {
    }
    
    private BeanFactory beanFactory;
    
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }
    
    public FormDefinitionService getFormDefinitionService() {
        return (FormDefinitionService) getService("FormService");
    }
    
    public TaskDefinitionService getTaskDefinitionService() {
        return (TaskDefinitionService) getService("TaskService");
    }
    
    public FileService getFileService() {
        return (FileService) getService("FileService");
    }
    
    public MenuService getMenuService() {
        return (MenuService) getService("MenuService");
    }
    
    private Object getService(String name) {
        String strategy = (String) this.beanFactory.getBean("strategy");
        return this.beanFactory.getBean(strategy + name);
    }
}
