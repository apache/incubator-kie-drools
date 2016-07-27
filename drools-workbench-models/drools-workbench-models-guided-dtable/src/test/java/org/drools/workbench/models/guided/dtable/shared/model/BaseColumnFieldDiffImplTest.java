/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.workbench.models.guided.dtable.shared.model;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class BaseColumnFieldDiffImplTest {

    @Parameterized.Parameter(0)
    public DTCellValue52 dcv1;

    @Parameterized.Parameter(1)
    public DTCellValue52 dcv2;

    @Parameterized.Parameter(2)
    public boolean isEqual;

    @Parameterized.Parameters
    public static Iterable<Object[]> data() {
        return new ArrayList<Object[]>() {{
            add( new Object[]{ null, null, true } );
            add( new Object[]{ new DTCellValue52( "hello" ), null, false } );
            add( new Object[]{ new DTCellValue52( 1 ), null, false } );
            add( new Object[]{ new DTCellValue52( 1.0 ), null, false } );
            add( new Object[]{ new DTCellValue52( 1.0f ), null, false } );
            add( new Object[]{ new DTCellValue52( 1L ), null, false } );
            add( new Object[]{ null, new DTCellValue52( "hello" ), false } );
            add( new Object[]{ null, new DTCellValue52( 1 ), false } );
            add( new Object[]{ null, new DTCellValue52( 1.0 ), false } );
            add( new Object[]{ null, new DTCellValue52( 1.0f ), false } );
            add( new Object[]{ null, new DTCellValue52( 1L ), false } );
            add( new Object[]{ new DTCellValue52( "hello" ), new DTCellValue52( "hello" ), true } );
            add( new Object[]{ new DTCellValue52( 1 ), new DTCellValue52( 1 ), true } );
            add( new Object[]{ new DTCellValue52( 1.0 ), new DTCellValue52( 1.0 ), true } );
            add( new Object[]{ new DTCellValue52( 1.0f ), new DTCellValue52( 1.0f ), true } );
            add( new Object[]{ new DTCellValue52( 1L ), new DTCellValue52( 1L ), true } );
        }};
    }

    @Test
    public void check() {
        assertEquals( isEqual,
                      BaseColumnFieldDiffImpl.isEqualOrNull( dcv1,
                                                             dcv2 ) );
    }

}
