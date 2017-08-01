/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.runtime.manager.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.kie.internal.runtime.Cacheable;
import org.kie.internal.runtime.manager.CacheManager;

public class CacheManagerImplTest {

	@Test
	public void testDispose() throws Exception {
		CacheManager cacheManager = new CacheManagerImpl();
		Cacheable cacheable = mock(Cacheable.class);
		Cacheable otherCacheable = mock(Cacheable.class);
		Object cached = new Object();

		cacheManager.add("cacheable", cacheable);
		cacheManager.add("other_cacheable", otherCacheable);
		cacheManager.add("cached", cached);

		// verify that objects have been added correctly
		assertEquals(cacheable, cacheManager.get("cacheable"));
		assertEquals(otherCacheable, cacheManager.get("other_cacheable"));
		assertEquals(cached, cacheManager.get("cached"));

		cacheManager.dispose();

		// cache should be empty after dispose
		assertNull(cacheManager.get("cacheable"));
		assertNull(cacheManager.get("other_cacheable"));
		assertNull(cacheManager.get("cached"));

		// close() method has been called on cached objects which implement Cacheable
		verify(cacheable).close();
		verify(otherCacheable).close();	
	}

}
