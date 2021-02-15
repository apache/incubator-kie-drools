/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package $Package$;


import org.kie.kogito.services.event.AbstractProcessDataEvent;

public class $TypeName$ extends AbstractProcessDataEvent<$Type$> {
    
    
    public $TypeName$() { }
    
    
    public $TypeName$(String source,
                      $Type$ body,
                      String kogitoProcessinstanceId,
                      String kogitoParentProcessinstanceId,
                      String kogitoRootProcessinstanceId,
                      String kogitoProcessId,
                      String kogitoRootProcessId,
                      String kogitoProcessinstanceState,
                      String kogitoReferenceId) {
        this(
            "$TypeName$",
            source,
            body,
            kogitoProcessinstanceId,
            kogitoParentProcessinstanceId,
            kogitoRootProcessinstanceId,
            kogitoProcessId,
            kogitoRootProcessId,
            kogitoProcessinstanceState,
            kogitoReferenceId);
    }
    
    
    public $TypeName$(String type, String source,
                      $Type$ body,
                      String kogitoProcessinstanceId,
                      String kogitoParentProcessinstanceId,
                      String kogitoRootProcessinstanceId,
                      String kogitoProcessId,
                      String kogitoRootProcessId,
                      String kogitoProcessinstanceState,
                      String kogitoReferenceId) {
        super(
            type,
            source,
            body,
            kogitoProcessinstanceId,
            kogitoParentProcessinstanceId,
            kogitoRootProcessinstanceId,
            kogitoProcessId,
            kogitoRootProcessId,
            kogitoProcessinstanceState,
            null,
            kogitoReferenceId);
    }
}
