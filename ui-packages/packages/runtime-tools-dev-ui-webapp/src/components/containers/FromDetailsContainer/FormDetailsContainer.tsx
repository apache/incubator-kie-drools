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

import React, { useEffect } from 'react';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/ouia-tools';
import { useHistory } from 'react-router-dom';
import { FormDetailsGatewayApi } from '../../../channel/FormDetails';
import { useFormDetailsGatewayApi } from '../../../channel/FormDetails/FormDetailsContext';
import { EmbeddedFormDetails } from '@kogito-apps/form-details';

const FormDetailsContainer: React.FC<OUIAProps> = ({ ouiaId, ouiaSafe }) => {
  const history = useHistory();
  const gatewayApi: FormDetailsGatewayApi = useFormDetailsGatewayApi();
  useEffect(() => {
    const unSubscribeHandler = gatewayApi.onOpenFormDetailsListener({
      onOpen(name: string) {
        history.push(`/`);
        history.push(`/Forms/${name}`);
      }
    });

    return () => {
      unSubscribeHandler.unSubscribe();
    };
  }, []);

  return (
    <EmbeddedFormDetails
      {...componentOuiaProps(ouiaId, 'form-details-container', ouiaSafe)}
      driver={gatewayApi}
      targetOrigin={'*'}
    />
  );
};

export default FormDetailsContainer;
