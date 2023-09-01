package org.drools.core.phreak;

public class PhreakNetworkNodeFactoryImpl implements PhreakNetworkNodeFactory {

    @Override
    public PhreakJoinNode createPhreakJoinNode() {
        return new PhreakJoinNode();
    }

    @Override
    public PhreakEvalNode createPhreakEvalNode() {
        return new PhreakEvalNode();
    }

    @Override
    public PhreakFromNode createPhreakFromNode() {
        return new PhreakFromNode();
    }

    @Override
    public PhreakReactiveFromNode createPhreakReactiveFromNode() {
        return new PhreakReactiveFromNode();
    }

    @Override
    public PhreakNotNode createPhreakNotNode() {
        return new PhreakNotNode();
    }

    @Override
    public PhreakExistsNode createPhreakExistsNode() {
        return new PhreakExistsNode();
    }

    @Override
    public PhreakAccumulateNode createPhreakAccumulateNode() {
        return new PhreakAccumulateNode();
    }

    @Override
    public PhreakGroupByNode createPhreakGroupByNode() {
        return new PhreakGroupByNode();
    }

    @Override
    public PhreakBranchNode createPhreakBranchNode() {
        return new PhreakBranchNode();
    }

    @Override
    public PhreakQueryNode createPhreakQueryNode() {
        return new PhreakQueryNode();
    }

    @Override
    public PhreakTimerNode createPhreakTimerNode() {
        return new PhreakTimerNode();
    }

    @Override
    public PhreakAsyncSendNode createPhreakAsyncSendNode() {
        return new PhreakAsyncSendNode();
    }

    @Override
    public PhreakAsyncReceiveNode createPhreakAsyncReceiveNode() {

        return new PhreakAsyncReceiveNode();
    }

    @Override
    public PhreakRuleTerminalNode createPhreakRuleTerminalNode() {
        return new PhreakRuleTerminalNode();
    }

    @Override
    public PhreakQueryTerminalNode createPhreakQueryTerminalNode() {
        return new PhreakQueryTerminalNode();
    }
}
