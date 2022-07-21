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
package com.adobe.cq.email.core.components.models;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.adobe.cq.email.core.components.services.AccLinkService;
import com.adobe.cq.wcm.core.components.commons.link.Link;
import com.adobe.cq.wcm.core.components.models.ListItem;
import com.adobe.cq.wcm.core.components.models.Teaser;
import com.adobe.cq.wcm.core.components.models.datalayer.ComponentData;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeaserModelTest {
    @Mock
    private Teaser delegate;
    @Mock
    private Resource inheritedResource;
    @Mock
    private SlingHttpServletRequest slingHttpServletRequest;
    @Mock
    private AccLinkService accLinkService;
    @Mock
    private ResourceResolver resourceResolver;
    @Mock
    private Link accLink;

    @Test
    void initModelWithNullDelegate() {
        TeaserModel sut = new TeaserModel();
        sut.initModel();
        verifyZeroInteractions(delegate, inheritedResource, accLinkService);
    }

    @Test
    void initModelWithNullInheritedResource() {
        TeaserModel sut = new TeaserModel();
        sut.delegate = delegate;
        sut.initModel();
        verifyZeroInteractions(delegate, inheritedResource, accLinkService);
    }

    @Test
    void initModelSuccess() {
        TeaserModel sut = initModel();
        sut.initModel();
        assertEquals(accLink, sut.getLink());
    }

    @Test
    void isActionsEnabledNullDelegate() {
        TeaserModel sut = new TeaserModel();
        assertFalse(sut.isActionsEnabled());
    }

    @Test
    void isActionsEnabled() {
        TeaserModel sut = new TeaserModel();
        sut.delegate = delegate;
        when(delegate.isActionsEnabled()).thenReturn(true);
        assertTrue(sut.isActionsEnabled());
    }

    @Test
    void getActionsNullDelegate() {
        TeaserModel sut = new TeaserModel();
        assertNull(sut.getActions());
    }

    @Test
    void getActions() {
        TeaserModel sut = initModel();
        String id = "ID";
        ComponentData data = mock(ComponentData.class);
        String appliedCssClasses = "APPLIED_CSS_CLASSES";
        String exportedType = "EXPORTED_TYPE";
        String url = "/url";
        String title = "TITLE";
        String description = "DESCRIPTION";
        Calendar lastModified = Calendar.getInstance();
        lastModified.clear();
        lastModified.set(2022, Calendar.JUNE, 13, 10, 0, 55);
        String path = "PATH";
        String name = "NAME";
        Resource teaserResource = mock(Resource.class);
        String linkUrl = "/another/link/url";
        TeaserListItem first = new TeaserListItem();
        first.setId(id);
        first.setData(data);
        first.setAppliedCssClasses(appliedCssClasses);
        first.setExportedType(exportedType);
        Link link = mock(Link.class);
        when(link.getURL()).thenReturn(linkUrl);
        first.setLink(link);
        first.setUrl(url);
        first.setTitle(title);
        first.setDescription(description);
        first.setLastModified(lastModified);
        first.setPath(path);
        first.setName(name);
        first.setTeaserResource(teaserResource);
        TeaserListItem second = new TeaserListItem();
        second.setId(id);
        second.setData(data);
        second.setAppliedCssClasses(appliedCssClasses);
        second.setExportedType(exportedType);
        second.setUrl(url);
        second.setTitle(title);
        second.setDescription(description);
        second.setLastModified(lastModified);
        second.setPath(path);
        second.setName(name);
        second.setTeaserResource(teaserResource);
        when(delegate.getActions()).thenReturn(Arrays.asList(first, second));
        sut.initModel();
        List<ListItem> actions = sut.getActions();
        assertEquals(2, actions.size());
        Link firstLink = actions.get(0).getLink();
        assertNotNull(firstLink);
        assertEquals(linkUrl, firstLink.getURL());
        Link secondLink = actions.get(1).getLink();
        assertNull(secondLink);
    }

    @Test
    void getLinkNullDelegate() {
        TeaserModel sut = new TeaserModel();
        assertNull(sut.getLink());
    }

    @Test
    void getLink() {
        TeaserModel sut = new TeaserModel();
        sut.delegate = delegate;
        Link link = mock(Link.class);
        when(delegate.getLink()).thenReturn(link);
        assertEquals(link, sut.getLink());
    }

    @Test
    void getLinkURLNullDelegate() {
        TeaserModel sut = new TeaserModel();
        assertNull(sut.getLinkURL());
    }

    @Test
    void getLinkURL() {
        TeaserModel sut = new TeaserModel();
        sut.delegate = delegate;
        String linkUrl = "/link/url";
        when(delegate.getLinkURL()).thenReturn(linkUrl);
        assertEquals(linkUrl, sut.getLinkURL());
    }

    @Test
    void getImageResourceNullDelegate() {
        TeaserModel sut = new TeaserModel();
        assertNull(sut.getImageResource());
    }

    @Test
    void getImageResource() {
        TeaserModel sut = new TeaserModel();
        sut.delegate = delegate;
        Resource resource = mock(Resource.class);
        when(delegate.getImageResource()).thenReturn(resource);
        assertEquals(resource, sut.getImageResource());
    }

    @Test
    void isImageLinkHiddenNullDelegate() {
        TeaserModel sut = new TeaserModel();
        assertFalse(sut.isImageLinkHidden());
    }

    @Test
    void isImageLinkHiddenLink() {
        TeaserModel sut = new TeaserModel();
        sut.delegate = delegate;
        when(delegate.isImageLinkHidden()).thenReturn(true);
        assertTrue(sut.isImageLinkHidden());
    }

    @Test
    void getTitleNullDelegate() {
        TeaserModel sut = new TeaserModel();
        assertNull(sut.getTitle());
    }

    @Test
    void getTitle() {
        TeaserModel sut = new TeaserModel();
        sut.delegate = delegate;
        String title = "TITLE";
        when(delegate.getTitle()).thenReturn(title);
        assertEquals(title, sut.getTitle());
    }

    @Test
    void getPreTitleNullDelegate() {
        TeaserModel sut = new TeaserModel();
        assertNull(sut.getPretitle());
    }

    @Test
    void getPreTitle() {
        TeaserModel sut = new TeaserModel();
        sut.delegate = delegate;
        String pretitle = "PRETITLE";
        when(delegate.getPretitle()).thenReturn(pretitle);
        assertEquals(pretitle, sut.getPretitle());
    }

    @Test
    void isTitleLinkHiddenNullDelegate() {
        TeaserModel sut = new TeaserModel();
        assertFalse(sut.isTitleLinkHidden());
    }

    @Test
    void isTitleLinkHiddenLink() {
        TeaserModel sut = new TeaserModel();
        sut.delegate = delegate;
        when(delegate.isTitleLinkHidden()).thenReturn(true);
        assertTrue(sut.isTitleLinkHidden());
    }

    @Test
    void getDescriptionNullDelegate() {
        TeaserModel sut = new TeaserModel();
        assertNull(sut.getDescription());
    }

    @Test
    void getDescription() {
        TeaserModel sut = new TeaserModel();
        sut.delegate = delegate;
        String description = "DESCRIPTION";
        when(delegate.getDescription()).thenReturn(description);
        assertEquals(description, sut.getDescription());
    }

    @Test
    void getDescriptionEmptyText() {
        TeaserModel sut = new TeaserModel();
        sut.delegate = delegate;
        String description = "";
        when(delegate.getDescription()).thenReturn(description);
        assertEquals(description, sut.getDescription());
    }

    @Test
    void getDescriptionWithLink() {
        TeaserModel sut = new TeaserModel();
        sut.delegate = delegate;
        String description = "<p>Some <a href=\"%3C%= targetData.link%20%&gt;\">link</a></p>";
        String expected = "<p>Some <a href=\"<%= targetData.link %>\" x-cq-linkchecker=\"skip\">link</a></p>";
        when(delegate.getDescription()).thenReturn(description);
        assertEquals(expected, sut.getDescription());
    }

    @Test
    void getTitleTypeNullDelegate() {
        TeaserModel sut = new TeaserModel();
        assertNull(sut.getTitleType());
    }

    @Test
    void getTitleType() {
        TeaserModel sut = new TeaserModel();
        sut.delegate = delegate;
        String titleType = "TITLE_TYPE";
        when(delegate.getTitleType()).thenReturn(titleType);
        assertEquals(titleType, sut.getTitleType());
    }

    private TeaserModel initModel() {
        TeaserModel sut = new TeaserModel();
        sut.delegate = delegate;
        sut.inheritedResource = inheritedResource;
        sut.slingHttpServletRequest = slingHttpServletRequest;
        sut.accLinkService = accLinkService;
        sut.resourceResolver = resourceResolver;
        ValueMap valueMap = mock(ValueMap.class);
        when(valueMap.get(eq("linkURL"), eq(String.class))).thenReturn("/link/url");
        when(inheritedResource.getValueMap()).thenReturn(valueMap);
        when(accLinkService.create(eq(resourceResolver), eq(slingHttpServletRequest), eq("/link/url"))).thenReturn(accLink);
        return sut;
    }
}
