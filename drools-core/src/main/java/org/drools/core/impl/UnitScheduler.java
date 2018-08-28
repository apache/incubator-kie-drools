package org.drools.core.impl;

import java.util.LinkedList;

import org.drools.core.spi.Activation;

public class UnitScheduler {

    private final LinkedList<UnitSession> units = new LinkedList<>();
    private UnitSession stackPointer;

    public UnitScheduler() {

    }

    public void schedule(UnitSession session) {
        stackPointer = null;
        units.push(session);
    }

    public void schedule(UnitSession session, Activation activation) {
        if (isActivatedUnit(stackPointer, activation)) {
            current().yield(session.unit());
            schedule(stackPointer);
            schedule(session);
        } else {
            scheduleRelative(session, activation);
        }
    }

    public void scheduleRelative(UnitSession session, Activation activation) {
        int idx = findActivatedUnit(activation);
        units.add(idx, session);
    }

    public int findActivatedUnit(Activation activation) {
        for (int i = 0; i < units.size(); i++) {
            if (isActivatedUnit(units.get(i), activation)) {
                return i;
            }
        }
        throw new ArrayIndexOutOfBoundsException("Cannot find Unit for activation " + activation);
    }

    private boolean isActivatedUnit(UnitSession unitSession, Activation activation) {
        return unitSession != null &&
                unitSession.unit().getClass().getName().equals(
                        activation.getRule().getRuleUnitClassName());
    }

    public UnitSession current() {
        return stackPointer;
    }

    public UnitSession next() {
        if (stackPointer != null) {
            stackPointer.getGuards().stream()
                    .filter(GuardedRuleUnitSession::isActive)
                    .forEach(units::add);
        }
        stackPointer = units.poll();
        return stackPointer;
    }

    public void halt() {
        current().halt();
    }

    public void registerGuard(GuardedRuleUnitSession session, Activation activation) {
        stackPointer.registerGuard(session, activation);
        session.addActivation(activation);
    }

    public void unregisterGuard(Activation activation) {
        if (stackPointer != null) {
            stackPointer.unregisterGuard(activation);
        }
    }

    @Override
    public String toString() {
        return "UnitScheduler(" + stackPointer + "::" + units + ")";
    }
}
