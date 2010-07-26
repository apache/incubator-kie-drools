/**
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.spi;

import java.io.Serializable;

/**
 * Interface for specifying truthness duration.
 * 
 * @see org.drools.rule.Rule#setDuration
 * @see org.drools.rule.Rule#getDuration
 * 
 * @author <a href="mailto:bob@werken.com">bob mcwhirter </a>
 */
public interface Duration
    extends
    Serializable {
    /**
     * <p>
     * Retrieve the duration for which the conditions of this <code>Tuple</code>
     * must remain true before the rule will fire.
     * </p>
     * 
     * @param tuple
     * 
     * @return the duration
     */
    long getDuration(Tuple tuple);
}
