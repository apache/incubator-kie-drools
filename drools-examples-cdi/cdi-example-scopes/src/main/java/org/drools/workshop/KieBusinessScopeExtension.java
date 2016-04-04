/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.workshop;

import java.io.Serializable;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

/**
 *
 * @author salaboy
 */
public class KieBusinessScopeExtension implements Extension, Serializable {

    public void addACustomScope(@Observes final BeforeBeanDiscovery event) {
        event.addScope(KieBusinessScoped.class, true, false);
    }

    public void registerACustomScopeContext(@Observes final AfterBeanDiscovery event) {
        event.addContext(new KieBusinessScopeContext());
    }
}
