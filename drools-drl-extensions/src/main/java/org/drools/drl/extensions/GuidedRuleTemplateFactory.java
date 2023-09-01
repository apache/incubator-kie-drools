package org.drools.drl.extensions;

import org.kie.api.internal.utils.KieService;

public class GuidedRuleTemplateFactory {

    private static class LazyHolder {
        private static final GuidedRuleTemplateProvider provider = KieService.load( GuidedRuleTemplateProvider.class );
    }

    public static GuidedRuleTemplateProvider getGuidedRuleTemplateProvider() {
        return LazyHolder.provider;
    }
}
