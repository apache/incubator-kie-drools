package org.drools.core.io.impl;

import org.apache.commons.lang3.ArrayUtils;
import org.drools.io.ByteArrayResource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(Parameterized.class)
public class ByteArrayResourceToStringTest {

    @Parameterized.Parameters(name = "{index}: bytes={0}, encoding={1}")
    public static Collection<Object[]> data() {
        return new ArrayList<Object[]>(Arrays.asList(new Object[][]{
                {
                        Arrays.asList(new Byte[]{10, 20, 30, 40}),
                        null,
                        "ByteArrayResource[bytes=[10, 20, 30, 40], encoding=null]"
                },
                {
                        Arrays.asList(new Byte[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100}),
                        null,
                        "ByteArrayResource[bytes=[10, 20, 30, 40, 50, 60, 70, 80, 90, 100], encoding=null]"
                },
                {
                        Arrays.asList(new Byte[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120}),
                        null,
                        "ByteArrayResource[bytes=[10, 20, 30, 40, 50, 60, 70, 80, 90, 100, ...], encoding=null]"
                },
                // non-null encoding
                {
                        Arrays.asList(new Byte[]{10, 20, 30, 40, 50, 60, 70, 80, 90, 100, 110, 120}),
                        "UTF-8",
                        "ByteArrayResource[bytes=[10, 20, 30, 40, 50, 60, 70, 80, 90, 100, ...], encoding=UTF-8]"
                },
                }));
    }

    // using List<Byte> instead of directly byte[] to make sure the bytes are printed as part of the test name
    // see above ({index}: bytes[{0}], encoding[{1}]) -- Array.toString only return object id
    @Parameterized.Parameter(0)
    public List<Byte> bytes;

    @Parameterized.Parameter(1)
    public String encoding;

    @Parameterized.Parameter(2)
    public String expectedString;

    @Test
    public void testToString() {
        byte[] byteArray = ArrayUtils.toPrimitive(bytes.toArray(new Byte[0]));
        ByteArrayResource byteArrayResource = new ByteArrayResource(byteArray, encoding);
        assertThat(byteArrayResource.toString()).isEqualTo(expectedString);
    }

}
