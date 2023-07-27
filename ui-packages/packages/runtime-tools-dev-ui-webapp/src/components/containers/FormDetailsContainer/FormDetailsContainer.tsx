/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import { useDevUIAppContext } from '../../contexts/DevUIAppContext';

interface FormDetailsContainerProps {
  formData: FormInfo;
  onSuccess: () => void;
  onError: (details?: string) => void;
}
const FormDetailsContainer: React.FC<FormDetailsContainerProps & OUIAProps> = ({
  formData,
  onSuccess,
  onError,
  ouiaId,
  ouiaSafe
}) => {
  const gatewayApi: FormDetailsGatewayApi = useFormDetailsGatewayApi();
  const appContext = useDevUIAppContext();

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
      targetOrigin={appContext.getDevUIUrl()}
      formData={formData}
    />
  );
};

export default FormDetailsContainer;
