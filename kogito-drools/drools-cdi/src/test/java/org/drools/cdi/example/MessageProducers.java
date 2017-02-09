/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.cdi.example;

import javax.enterprise.inject.Produces;

public class MessageProducers {

    @Produces @Msg1 
    public String getSimple1() {
        return "msg.1";
    }
    
    @Msg2 @Produces 
    public String getSimple2() {
        return "msg.2";
    }
    
    @Produces @Msg("named1") 
    public String getNamed1() {
        return "msg.named1";
    }
    
    @Produces @Msg("named2") 
    public String getNamed2() {
        return "msg.named2";
    }   
         
}
