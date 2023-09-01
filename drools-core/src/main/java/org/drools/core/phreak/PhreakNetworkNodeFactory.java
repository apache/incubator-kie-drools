package org.drools.core.phreak;

import org.kie.api.internal.utils.KieService;

public interface PhreakNetworkNodeFactory extends KieService {

    PhreakJoinNode createPhreakJoinNode();

    PhreakEvalNode createPhreakEvalNode();

    PhreakFromNode createPhreakFromNode();

    PhreakReactiveFromNode createPhreakReactiveFromNode();

    PhreakNotNode createPhreakNotNode();

    PhreakExistsNode createPhreakExistsNode();

    PhreakAccumulateNode createPhreakAccumulateNode();

    PhreakGroupByNode createPhreakGroupByNode();

    PhreakBranchNode createPhreakBranchNode();

    PhreakQueryNode createPhreakQueryNode();

    PhreakTimerNode createPhreakTimerNode();

    PhreakAsyncSendNode createPhreakAsyncSendNode();

    PhreakAsyncReceiveNode createPhreakAsyncReceiveNode();

    PhreakRuleTerminalNode createPhreakRuleTerminalNode();

    PhreakQueryTerminalNode createPhreakQueryTerminalNode();

    class Factory {

        private static class LazyHolder {

            private static final PhreakNetworkNodeFactory INSTANCE = createInstance();

            private static PhreakNetworkNodeFactory createInstance() {
                PhreakNetworkNodeFactory factory = KieService.load(PhreakNetworkNodeFactory.class);
                return factory != null ? factory : new PhreakNetworkNodeFactoryImpl();
            }
        }

        public static PhreakNetworkNodeFactory get() {
            return LazyHolder.INSTANCE;
        }

        private Factory() {}
    }
}
