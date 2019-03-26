/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.process.workitem.email;

import java.util.ArrayList;
import java.util.List;

public class Recipients {

    private List<Recipient> list = new ArrayList<Recipient>();

    public void setRecipients(List<Recipient> recipients) {
        for (Recipient recipient : recipients) {
            addRecipient(recipient);
        }
    }

    public boolean addRecipient(Recipient recipient) {
        if (!this.list.contains(recipient)) {
            this.list.add(recipient);
            return true;
        }
        return false;
    }

    public boolean removeRecipient(Recipient recipient) {
        return this.list.remove(recipient);
    }

    public List<Recipient> getRecipients() {
        return this.list;
    }

    public Recipient[] toArray() {
        return (Recipient[]) list.toArray(new Recipient[list.size()]);
    }

    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((list == null) ? 0 : list.hashCode());
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Recipients other = (Recipients) obj;
        if (list == null) {
            if (other.list != null) {
                return false;
            }
        } else if (!list.equals(other.list)) {
            return false;
        }
        return true;
    }
}