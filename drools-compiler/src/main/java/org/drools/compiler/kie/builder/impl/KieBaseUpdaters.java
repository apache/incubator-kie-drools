package org.drools.compiler.kie.builder.impl;

import java.util.Collection;

import org.kie.api.internal.utils.KieService;

public interface KieBaseUpdaters extends KieService {

    Collection<KieBaseUpdaterFactory> getChildren();
}
