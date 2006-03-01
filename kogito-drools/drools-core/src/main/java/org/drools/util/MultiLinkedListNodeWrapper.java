/*
 * Copyright 2005 JBoss Inc
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

package org.drools.util;

/**
 * MultiLinkedListNodeWrapper
 * A wrapper to a MultiLinkedListNode in a way you can add it to 
 * another linked list without losing the references to the original
 * list.
 *
 * @author <a href="mailto:edson.tirelli@auster.com.br">Edson Tirelli</a>
 *
 * Created: 12/02/2006
 */
public class MultiLinkedListNodeWrapper extends BaseMultiLinkedListNode {

   private LinkedListNode node = null;
   
   public MultiLinkedListNodeWrapper(LinkedListNode node) {
       this.node = node;
   }
   
   public LinkedListNode getNode() {
       return this.node;
   }
    
}
