package org.kie.dmn.model.api;

import javax.xml.stream.Location;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class RowLocationTest {

    @Test
    public void smokeTest() {
        RowLocation ut = new RowLocation(new DummyLocation());
        assertThat(ut.getCharacterOffset()).isEqualTo(-1);
        assertThat(ut.getColumnNumber()).isEqualTo(-1);
        assertThat(ut.toString()).hasSizeGreaterThan(0);
    }

    private static class DummyLocation implements Location {

        @Override
        public int getLineNumber() {
            return 47;
        }

        @Override
        public int getColumnNumber() {
            return -1;
        }

        @Override
        public int getCharacterOffset() {
            return -1;
        }

        @Override
        public String getPublicId() {
            return "publicId";
        }

        @Override
        public String getSystemId() {
            return "systemId";
        }

    }
}
