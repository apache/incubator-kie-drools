/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
import React from 'react';
import {
  componentOuiaProps,
  OUIAProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import { FormDetailsGatewayApi } from '../../../channel/FormDetails';
import { useFormDetailsGatewayApi } from '../../../channel/FormDetails/FormDetailsContext';
import { EmbeddedFormDetails, FormContent } from '@kogito-apps/form-details';
import { FormInfo } from '@kogito-apps/forms-list';
import { Form } from '@kogito-apps/form-displayer';

interface FormDetailsContainerProps {
  formData: FormInfo;
  onSuccess: () => void;
  onError: (details?: string) => void;
  targetOrigin: string;
}
const FormDetailsContainer: React.FC<FormDetailsContainerProps & OUIAProps> = ({
  formData,
  onSuccess,
  onError,
  ouiaId,
  ouiaSafe,
  targetOrigin
}) => {
  const gatewayApi: FormDetailsGatewayApi = useFormDetailsGatewayApi();

  return (
    <EmbeddedFormDetails
      {...componentOuiaProps(ouiaId, 'form-details-container', ouiaSafe)}
      driver={{
        getFormContent: function (formName: string): Promise<Form> {
          return gatewayApi.getFormContent(formName);
        },
        saveFormContent(formName: string, content: FormContent): void {
          gatewayApi
            .saveFormContent(formName, content)
            .then((value) => onSuccess())
            .catch((error) => {
              const message = error.response
                ? error.response.data
                : error.message;
              onError(message);
            });
        }
      }}
      targetOrigin={targetOrigin}
      formData={formData}
    />
  );
};

export default FormDetailsContainer;
