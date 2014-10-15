package org.drools.core.util.bitmask;

public class AllSetButLastBitMask extends SingleLongBitMask implements BitMask, AllSetMask {

    private static final AllSetButLastBitMask INSTANCE = new AllSetButLastBitMask();

    private AllSetButLastBitMask() { }

    public static AllSetButLastBitMask get() {
        return INSTANCE;
    }

    @Override
    public BitMask set(int index) {
        return index == 0 ? AllSetBitMask.get() : this;
    }

    @Override
    public BitMask setAll(BitMask mask) {
        return mask.isSet(0) ? AllSetBitMask.get() : this;
    }

    @Override
    public BitMask reset(int index) {
        if (index == 0) {
            return this;
        }
        return BitMask.Factory.getFull(index+1).reset(0).reset(index);
    }

    @Override
    public BitMask resetAll(BitMask mask) {
        if (mask instanceof EmptyMask) {
            return this;
        }
        if (mask instanceof AllSetMask) {
            return EmptyBitMask.get();
        }
        return BitMask.Factory.getFull(mask instanceof LongBitMask ? 1 : 65).reset(0).resetAll(mask);
    }

    @Override
    public boolean isSet(int index) {
        return index != 0;
    }

    @Override
    public boolean isAllSet() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean intersects(BitMask mask) {
        if (mask instanceof AllSetMask) {
            return true;
        }
        if (mask instanceof EmptyMask) {
            return false;
        }
        return mask instanceof LongBitMask ?
               (Long.MAX_VALUE & ((LongBitMask)mask).asLong()) != 0 :
               ((OpenBitSet)mask).nextSetBit(1) != -1;
    }

    @Override
    public long asLong() {
        return Long.MAX_VALUE;
    }

    @Override
    public AllSetButLastBitMask clone() {
        return this;
    }

    @Override
    public String getInstancingStatement() {
        return AllSetButLastBitMask.class.getCanonicalName() + ".get()";
    }
}
