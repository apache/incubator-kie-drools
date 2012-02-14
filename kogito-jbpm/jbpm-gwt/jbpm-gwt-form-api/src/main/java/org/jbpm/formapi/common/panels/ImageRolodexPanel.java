/*
 * Copyright 2011 JBoss Inc
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
package org.jbpm.formapi.common.panels;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.impl.ClippedImagePrototype;
import com.yesmail.gwt.rolodex.client.RolodexCard;
import com.yesmail.gwt.rolodex.client.RolodexCardBundle;
import com.yesmail.gwt.rolodex.client.RolodexPanel;

public class ImageRolodexPanel extends RolodexPanel implements HasWidgets {

    public ImageRolodexPanel(ImageResource defaultImage, int maxHeight) {
        super(new ImageRolodexCardBundle(defaultImage, maxHeight), 0, null, true);
    }
    
    @Override
    public void add(Widget w) {
        if (w instanceof RolodexCard) {
            RolodexCard card = (RolodexCard) w;
            super.add(card);
        }
        // doesn't allow anything else
    }

    @Override
    public void clear() {
        cards.clear();
        panel.clear();
    }

    @Override
    public Iterator<Widget> iterator() {
        return new ImageRolodexIterator(this);
    }

    @Override
    public boolean remove(Widget w) {
        if (w instanceof RolodexCard) {
            RolodexCard card = (RolodexCard) w;
            cards.remove(card);
        }
        return panel.remove(w);
    }
    
    class ImageRolodexIterator implements Iterator<Widget> {

        private final ImageRolodexPanel panel;
        private final Iterator<RolodexCard> iterator;
        private Widget current = null;

        @SuppressWarnings("unchecked")
        public ImageRolodexIterator(ImageRolodexPanel panel) {
            this.panel = panel;
            this.iterator = panel.cards.iterator();
        }
        
        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public Widget next() {
            this.current = iterator.next();
            return current;
        }

        @Override
        public void remove() {
            if (current != null) {
                panel.panel.remove(current);
                current = null;
            }
            iterator.remove();
        }
    }
    
    static class ImageRolodexCardBundle implements RolodexCardBundle {

        private final int maxHeight;
        private final ImageResource defaultImage;
        
        public ImageRolodexCardBundle(ImageResource defaultImage, int maxHeight) {
            this.maxHeight = maxHeight;
            this.defaultImage = defaultImage;
        }

        @Override
        public int getMaxHeight() {
            return maxHeight;
        }

        @Override
        public RolodexCard[] getRolodexCards() {
            RolodexCard[] cards = new RolodexCard[3];
            for (int index = 0; index < 3; index++) {
                ClippedImagePrototype expanded = new ClippedImagePrototype(defaultImage.getURL(), 0, 0, 300, 200);
                ClippedImagePrototype collapseLeft = new ClippedImagePrototype(defaultImage.getURL(), 0, 0, 100, 100);
                ClippedImagePrototype collapseRight = new ClippedImagePrototype(defaultImage.getURL(), 0, 0, 100, 100);
                cards[index] = new RolodexCard(expanded, collapseLeft, collapseRight, 300, 100, 0);
            }
            return cards;
        }
        
    }

    public RolodexCard get(int index) {
        return (RolodexCard) cards.get(index);
    }

    public int size() {
        return cards.size();
    }

    @SuppressWarnings("unchecked")
    public List<RolodexCard> getCards() {
        return new ArrayList<RolodexCard>(cards);
    }

    public void setAnimated(boolean animated) {
        super.animate = animated;
    }
    
    public boolean isAnimated() {
        return super.animate;
    }
}
