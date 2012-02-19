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
package org.jbpm.formapi.shared.api.items;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jbpm.formapi.shared.api.FormItemRepresentation;
import org.jbpm.formapi.shared.form.FormEncodingException;

import com.gwtent.reflection.client.Reflectable;

@Reflectable
public class ImageRolodexRepresentation extends FormItemRepresentation {

    private List<String> imageUrls = new ArrayList<String>();
    private boolean animated = true;
    private int selectedIndex = 0;
    
    public ImageRolodexRepresentation() {
        super("imageRolodex");
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
    
    public boolean isAnimated() {
        return animated;
    }

    public void setAnimated(boolean animated) {
        this.animated = animated;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void setDataMap(Map<String, Object> data)
            throws FormEncodingException {
        super.setDataMap(data);
        List<Object> imagesMap = (List<Object>) data.get("imageUrls");
        imageUrls.clear();
        if (imagesMap != null) {
            for (Object obj : imagesMap) {
                imageUrls.add(obj.toString());
            }
        }
        Boolean anim = (Boolean) data.get("animated");
        this.animated = anim != null && anim;
        Integer ind = (Integer) data.get("selectedIndex");
        this.selectedIndex = ind == null ? 0 : ind;
    }
    
    @Override
    public Map<String, Object> getDataMap() {
        Map<String, Object> data = super.getDataMap();
        List<Object> imagesMap = new ArrayList<Object>();
        for (String imageUrl : imageUrls) {
            imagesMap.add(imageUrl);
        }
        data.put("imageUrls", imagesMap);
        data.put("animated", Boolean.valueOf(animated));
        data.put("selectedIndex", Integer.valueOf(selectedIndex));
        return data;
    }
    
    
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof ImageRolodexRepresentation)) return false;
        ImageRolodexRepresentation other = (ImageRolodexRepresentation) obj;
        boolean equals = (this.imageUrls == null && other.imageUrls == null) || 
            (this.imageUrls != null && this.imageUrls.equals(other.imageUrls));
        if (!equals) return equals;
        equals = (this.animated == other.animated);
        if (!equals) return equals;
        equals = this.selectedIndex == other.selectedIndex;
        return equals;
    }
    
    @Override
    public int hashCode() {
        int result = super.hashCode();
        int aux = this.imageUrls == null ? 0 : this.imageUrls.hashCode();
        result = 37 * result + aux;
        aux = this.animated ? 0 : 1;
        result = 37 * result + aux;
        result = 37 * result + this.selectedIndex;
        return result;
    }
}
