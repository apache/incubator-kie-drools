/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.backend.marshalling.v1_2.xstream;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.dmn.model.api.DMNExternalLink;
import org.kie.dmn.model.v1_2.TDMNExternalLink;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.dmn.backend.marshalling.v1_2.xstream.DMNExternalLinkConverter.LINK_DESCRIPTION;
import static org.kie.dmn.backend.marshalling.v1_2.xstream.DMNExternalLinkConverter.URL;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DMNExternalLinkConverterTest {

    private DMNExternalLinkConverter converter;

    @Mock
    private XStream stream;

    @Before
    public void setup() {
        converter = spy(new DMNExternalLinkConverter(stream));
    }

    @Test
    public void testCanConvert() {

        assertFalse(converter.canConvert(Object.class));
        assertTrue(converter.canConvert(TDMNExternalLink.class));
        assertTrue(converter.canConvert(DMNExternalLink.class));
    }

    @Test
    public void testAssignAttributes() {

        final HierarchicalStreamReader reader = mock(HierarchicalStreamReader.class);
        final String linkDescription = "link description";
        when(reader.getAttribute(LINK_DESCRIPTION)).thenReturn(linkDescription);

        final String url = "url";
        when(reader.getAttribute(URL)).thenReturn(url);

        final DMNExternalLink externalLink = mock(DMNExternalLink.class);

        doNothing().when(converter).superAssignAttributes(reader, externalLink);
        converter.assignAttributes(reader, externalLink);

        verify(externalLink).setURL(url);
        verify(externalLink).setLinkDescription(linkDescription);
    }

    @Test
    public void testWriteAttributes() {

        final HierarchicalStreamWriter writer = mock(HierarchicalStreamWriter.class);
        final DMNExternalLink externalLink = mock(DMNExternalLink.class);

        final String linkDescription = "link description";
        when(externalLink.getLinkDescription()).thenReturn(linkDescription);

        final String url = "url";
        when(externalLink.getURL()).thenReturn(url);

        doNothing().when(converter).superWriteAttributes(writer, externalLink);
        converter.writeAttributes(writer, externalLink);

        verify(writer).addAttribute(LINK_DESCRIPTION, linkDescription);
        verify(writer).addAttribute(URL, url);
    }
}