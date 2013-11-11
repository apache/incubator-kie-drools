package org.drools.factmodel.traits;

import java.util.BitSet;

/**
 * Created with IntelliJ IDEA.
 * User: mamad
 * Date: 11/1/13
 * Time: 3:54 PM
 * To change this template use File | Settings | File Templates.
 */
public interface LatticeElement<T> {

    public T getValue();

    public BitSet getBitMask();
}
