/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.core.util.bitmask;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.lang.Long;

public class LongBitMaskTest {

  @Rule public final ExpectedException thrown = ExpectedException.none();

  @Test
  public void testSet() {
    Assert.assertEquals("1", new LongBitMask().set(0).toString());
    Assert.assertEquals("2", new LongBitMask().set(1).toString());
    Assert.assertEquals("0, 2", new LongBitMask().set(65).toString());
  }

  @Test
  public void testSetAll() {
      Assert.assertEquals("0",
          new LongBitMask().setAll(new LongBitMask()).toString());
      Assert.assertEquals("-1",
          new LongBitMask().setAll(AllSetBitMask.get()).toString());
      Assert.assertEquals("9223372036854775807",
          new LongBitMask().setAll(AllSetButLastBitMask.get()).toString());
      Assert.assertEquals("-1",
          new LongBitMask(1).setAll(AllSetButLastBitMask.get()).toString());
      Assert.assertEquals("0",
          new LongBitMask().setAll(new OpenBitSet()).toString());
      Assert.assertEquals("1",
          new LongBitMask().setAll(EmptyButLastBitMask.get()).toString());
      Assert.assertEquals("0",
          new LongBitMask().setAll(EmptyBitMask.get()).toString());
  }

  @Test
  public void testReset() {
    Assert.assertEquals("0", new LongBitMask().reset(0).toString());
    Assert.assertEquals("0", new LongBitMask().reset(1).toString());
    Assert.assertEquals("0", new LongBitMask().reset(65).toString());
  }

  @Test
  public void testResetAll() {
      Assert.assertEquals("0",
          new LongBitMask().resetAll(new LongBitMask()).toString());
      Assert.assertEquals("0",
          new LongBitMask().resetAll(AllSetBitMask.get()).toString());
      Assert.assertEquals("0",
          new LongBitMask().resetAll(AllSetButLastBitMask.get()).toString());
      Assert.assertEquals("0",
          new LongBitMask().resetAll(EmptyButLastBitMask.get()).toString());
      Assert.assertEquals("0",
          new LongBitMask().resetAll(EmptyBitMask.get()).toString());
      
      thrown.expect(RuntimeException.class);
      new LongBitMask().resetAll(new OpenBitSet()).toString();
  }

  @Test
  public void testIsSet() {
    Assert.assertFalse(new LongBitMask().set(1).isSet(0));
    Assert.assertTrue(new LongBitMask().set(1).isSet(1));
  }

  @Test
  public void testIsAllSet() {
    Assert.assertFalse(new LongBitMask().isAllSet());
    Assert.assertTrue(new LongBitMask(-1L).isAllSet());
  }

  @Test
  public void testIsEmpty() {
    Assert.assertFalse(new LongBitMask(1L).isEmpty());
    Assert.assertTrue(new LongBitMask(0L).isEmpty());
  }

  @Test
  public void testIntersects() {
    Assert.assertFalse(new LongBitMask(0L).intersects(EmptyBitMask.get()));
    Assert.assertFalse(new LongBitMask(0L).intersects(new LongBitMask(0L)));
    Assert.assertTrue(new LongBitMask(2L).intersects(new LongBitMask(2L)));
  }

  @Test
  public void testClone() {
    Assert.assertEquals(1L, new LongBitMask(1L).clone().asLong());
  }

  @Test
  public void testGetInstancingStatement() {
    Assert.assertEquals(
        "org.drools.core.util.bitmask.EmptyBitMask.get()",
        new LongBitMask(0L).getInstancingStatement());
    Assert.assertEquals(
        "org.drools.core.util.bitmask.EmptyButLastBitMask.get()",
        new LongBitMask(1L).getInstancingStatement());
      Assert.assertEquals(
        "org.drools.core.util.bitmask.AllSetButLastBitMask.get()",
        new LongBitMask(Long.MAX_VALUE).getInstancingStatement());
    Assert.assertEquals(
        "org.drools.core.util.bitmask.AllSetBitMask.get()",
        new LongBitMask(-1L).getInstancingStatement());
    Assert.assertEquals(
        "new org.drools.core.util.bitmask.LongBitMask(2L)",
        new LongBitMask(2L).getInstancingStatement());
  }
}
