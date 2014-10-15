package org.drools.core.util.bitmask;

public class AllSetBitMask extends SingleLongBitMask implements BitMask, AllSetMask {

    private static final AllSetBitMask INSTANCE = new AllSetBitMask();

    private AllSetBitMask() { }

    public static AllSetBitMask get() {
        return INSTANCE;
    }

    @Override
    public BitMask set(int index) {
        return this;
    }

    @Override
    public BitMask setAll(BitMask mask) {
        return this;
    }

    @Override
    public BitMask reset(int index) {
        return BitMask.Factory.getFull(index+1).reset(index);
    }

    @Override
    public BitMask resetAll(BitMask mask) {
        if (mask instanceof EmptyBitMask) {
            return this;
        } else if (mask instanceof EmptyButLastBitMask) {
            return AllSetButLastBitMask.get();
        } else if (mask instanceof AllSetBitMask) {
            return EmptyBitMask.get();
        } else if (mask instanceof AllSetButLastBitMask) {
            return EmptyButLastBitMask.get();
        }
        return BitMask.Factory.getFull(mask instanceof LongBitMask ? 1 : 65).resetAll(mask);
    }

    @Override
    public boolean isSet(int index) {
        return true;
    }

    @Override
    public boolean isAllSet() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean intersects(BitMask mask) {
        return !mask.isEmpty();
    }

    @Override
    public long asLong() {
        return -1L;
    }

    @Override
    public AllSetBitMask clone() {
        return this;
    }

    @Override
    public String getInstancingStatement() {
        return AllSetBitMask.class.getCanonicalName() + ".get()";
    }
}
