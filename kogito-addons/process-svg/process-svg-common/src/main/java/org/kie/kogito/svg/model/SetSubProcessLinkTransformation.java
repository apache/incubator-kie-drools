/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.svg.model;

import org.w3c.dom.Element;

public class SetSubProcessLinkTransformation extends NodeTransformation {

    private String link;

    public SetSubProcessLinkTransformation(String nodeId, String link) {
        super(nodeId);
        this.link = link;
    }

    public String getLink() {
        return link;
    }

    public void transform(SVGSummary summary) {
        NodeSummary node = summary.getNode(getNodeId());
        if (node != null) {
            Element linkNode = node.getSubProcessLink();
            if (linkNode != null) {
                linkNode.setAttribute("onclick", "");
                linkNode.setAttribute("xlink:href", link);
                linkNode.setAttribute("target", "_blank");
            }
        }
    }
}
