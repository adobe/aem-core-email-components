/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.email.core.components.internal.models;

import java.util.Calendar;

import org.apache.sling.api.resource.Resource;
import org.junit.jupiter.api.Test;

import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ListItemWrapperTest {

    @Test
    void testAllMethodsDelegated() {
        ListItem wrapped = mock(ListItem.class);
        ListItemWrapper subject = new ListItemWrapper(wrapped);

        Link<?> link = mock(Link.class);
        when(wrapped.getLink()).thenReturn(link);
        assertEquals(link, subject.getLink());
        verify(wrapped).getLink();

        when(wrapped.getURL()).thenReturn("http://foo.bar");
        assertEquals("http://foo.bar", subject.getURL());
        verify(wrapped).getURL();

        when(wrapped.getTitle()).thenReturn("Foo Bar");
        assertEquals("Foo Bar", subject.getTitle());
        verify(wrapped).getTitle();

        when(wrapped.getDescription()).thenReturn("Foo Bar");
        assertEquals("Foo Bar", subject.getDescription());
        verify(wrapped).getDescription();

        Calendar now = Calendar.getInstance();
        when(wrapped.getLastModified()).thenReturn(now);
        assertEquals(now, subject.getLastModified());
        verify(wrapped).getLastModified();

        when(wrapped.getPath()).thenReturn("/foobar");
        assertEquals("/foobar", subject.getPath());
        verify(wrapped).getPath();

        when(wrapped.getName()).thenReturn("Foo Bar");
        assertEquals("Foo Bar", subject.getName());
        verify(wrapped).getName();

        Resource resource = mock(Resource.class);
        when(wrapped.getTeaserResource()).thenReturn(resource);
        assertEquals(resource, subject.getTeaserResource());
        verify(wrapped).getTeaserResource();

        when(wrapped.getId()).thenReturn("Foo Bar");
        assertEquals("Foo Bar", subject.getId());
        verify(wrapped).getId();

        ComponentData data = mock(ComponentData.class);
        when(wrapped.getData()).thenReturn(data);
        assertEquals(data, subject.getData());
        verify(wrapped).getData();

        when(wrapped.getAppliedCssClasses()).thenReturn(".foo-bar");
        assertEquals(".foo-bar", subject.getAppliedCssClasses());
        verify(wrapped).getAppliedCssClasses();

        when(wrapped.getExportedType()).thenReturn("itemType");
        assertEquals("itemType", subject.getExportedType());
        verify(wrapped).getExportedType();
    }
}
