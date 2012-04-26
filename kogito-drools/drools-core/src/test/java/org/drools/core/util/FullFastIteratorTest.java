package org.drools.core.util;

import static org.junit.Assert.*;

import org.drools.core.util.RightTupleIndexHashTable.FullFastIterator;
import org.drools.reteoo.RightTuple;
import org.junit.Test;

public class FullFastIteratorTest {

  @Test
  public void testCanReachAllEntriesInLastTableRow() {

    // Construct a table with one row, containing one list, containing three entries.
    int numEntries = 3;
    RightTupleList[] table = new RightTupleList[1];
    table[0] = new RightTupleList();
    for (int i = 0; i < numEntries; i++) {
      table[0].add(new RightTuple());
    }

    // Iterate over the table. We should be able to visit all three entries.
    FullFastIterator iter = new RightTupleIndexHashTable.FullFastIterator(table);
    for (int i = 0; i < numEntries; i++) {
      assertNotNull("Could not visit entry at index " + i, iter.next(null));
    }
  }
}
