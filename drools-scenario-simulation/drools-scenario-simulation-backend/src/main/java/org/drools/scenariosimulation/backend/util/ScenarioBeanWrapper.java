package org.drools.scenariosimulation.backend.util;

public class ScenarioBeanWrapper<T> {

    private final Object bean;
    private final Class<T> beanClass;

    public ScenarioBeanWrapper(Object bean, Class<T> beanClass) {
        this.beanClass = beanClass;
        this.bean = bean;
    }

    public Object getBean() {
        return bean;
    }

    public Class<T> getBeanClass() {
        return beanClass;
    }
}
