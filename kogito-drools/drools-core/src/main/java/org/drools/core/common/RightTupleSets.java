package org.drools.core.common;

import org.drools.core.reteoo.LeftTuple;
import org.drools.core.reteoo.RightTuple;


public interface RightTupleSets {

    public RightTuple getInsertFirst();

    public RightTuple getDeleteFirst();

    public RightTuple getUpdateFirst();

//    public void resetInsert();
//
//    public void resetDelete() ;
//
//    public void resetUpdate() ;

    public void resetAll();

    public int insertSize();

    public int deleteSize();

    public int updateSize();

    public boolean addInsert(RightTuple rightTuple);

    public boolean addDelete(RightTuple rightTuple);

    public boolean addUpdate(RightTuple rightTuple);

//    public void removeInsert(RightTuple rightTuple);
//
//    public void removeDelete(RightTuple rightTuple);
//
//    public void removeUpdate(RightTuple rightTuple);

//    public void addAllInserts(RightTupleSets tupleSets);
//
//    public void addAllDeletes(RightTupleSets tupleSets);
//
//    public void addAllUpdates(RightTupleSets tupleSets);
//
//    public void addAll(RightTupleSets source);

//    public void clear();

    public RightTupleSets takeAll();

    public String toStringSizes();

    public String toString();

    public boolean isEmpty();

}
