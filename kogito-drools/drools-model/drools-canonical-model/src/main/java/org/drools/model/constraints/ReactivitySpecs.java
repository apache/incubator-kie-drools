/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.drools.model.constraints;

import org.drools.model.BitMask;
import org.drools.model.DomainClassMetadata;
import org.drools.model.bitmask.AllSetButLastBitMask;

public class ReactivitySpecs {

    public static final ReactivitySpecs EMPTY = new ReactivitySpecs();

    private final BitMask bitMask;
    private final String[] props;

    private ReactivitySpecs() {
        this.props = new String[0];
        this.bitMask = AllSetButLastBitMask.get();
    }

    public ReactivitySpecs( DomainClassMetadata metadata, String... props ) {
        this.props = props;
        this.bitMask = metadata != null ? BitMask.getPatternMask(metadata, props) : null;
    }

    public BitMask getBitMask() {
        return bitMask;
    }

    public String[] getProps() {
        return props;
    }
}
