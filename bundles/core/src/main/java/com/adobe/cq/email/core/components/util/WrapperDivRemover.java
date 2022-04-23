package com.adobe.cq.email.core.components.util;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WrapperDivRemover {
    private static final Logger LOG = LoggerFactory.getLogger(WrapperDivRemover.class);

    private WrapperDivRemover() {
        // to avoid instantiation
    }

    public static void removeWrapperDivs(Document doc, String[] wrapperDivClassesToBeRemoved) {
        if (Objects.isNull(doc) || Objects.isNull(wrapperDivClassesToBeRemoved) || wrapperDivClassesToBeRemoved.length == 0) {
            return;
        }
        Element parent = doc.parent();
        Elements children = doc.children();
        removeWrapperDivs(parent, children, wrapperDivClassesToBeRemoved);
        for (String wrapperDivClassToBeRemoved : wrapperDivClassesToBeRemoved) {
            Elements select = doc.getElementsByClass(wrapperDivClassToBeRemoved);
            for (Element element : select) {
                LOG.warn(element.toString());
            }
        }
    }

    private static void removeWrapperDivs(Element parent, Elements children, String[] wrapperDivClassesToBeRemoved) {
        if (Objects.isNull(children)||children.isEmpty()) {
            return;
        }
        for (Element child : children) {
            for (String wrapperDivClassToBeRemoved : wrapperDivClassesToBeRemoved) {
                if (child.is("." +wrapperDivClassToBeRemoved)) {
                    Elements removedDivChildren = child.children();
                    int index = child.siblingIndex();
                    child.remove();
                    parent.insertChildren(index, removedDivChildren);
                }
            }
        }
    }

}
