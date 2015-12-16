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

package org.drools.core.xml;

public class GenericsTest {
        
    public static class ResourceTypes {
        public static DecisionTableConf DTABLE = new DecisionTableConfImpl();
        public static XTable XTABLE = new XTableImpl();
    }

    public static interface Resource {
        <T> T setType(T t);
    }

    public static class ResourceImpl implements Resource {
        private InternalConf conf;
        public <T> T setType(T t) {
            conf = ((InternalConf)t).newConf();
            return (T) conf;
        }
    }
    
    public static interface ResourceConf {

    }
    
    public static interface InternalConf extends ResourceConf {
        public InternalConf newConf();
    }     

    public static interface DecisionTableConf extends ResourceConf {
        public void setWorksheetName(String name);
    }       
    
    public static class DecisionTableConfImpl implements DecisionTableConf, InternalConf {
        public void setWorksheetName(String name) {

        }

        public InternalConf newConf() {
            return new DecisionTableConfImpl();
        }
    }  
    
    public static interface XTable extends ResourceConf {
        public void setX(String name);
    }       
    
    public static class XTableImpl implements XTable, InternalConf {
        public void setX(String name) {

        }

        public InternalConf newConf() {
            return new XTableImpl();
        }
    }      
    
    
    public void test1() {
        Resource res1 = new ResourceImpl();
        res1.setType( ResourceTypes.DTABLE ).setWorksheetName( "xxxx" );
        
        Resource res2 = new ResourceImpl();
        res2.setType( ResourceTypes.XTABLE ).setX( "xxxx" );        
    }
}
