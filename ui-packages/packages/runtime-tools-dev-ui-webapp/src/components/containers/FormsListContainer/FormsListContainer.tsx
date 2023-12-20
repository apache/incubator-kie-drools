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
import React, { useEffect } from 'react';
import { OUIAProps } from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import { EmbeddedFormsList } from '@kogito-apps/forms-list';
import { FormInfo } from '@kogito-apps/components-common/dist/types';
import { FormsListGatewayApi } from '../../../channel/FormsList';
import { useFormsListGatewayApi } from '../../../channel/FormsList/FormsListContext';
import { useHistory } from 'react-router-dom';
import { useDevUIAppContext } from '../../contexts/DevUIAppContext';

const FormsListContainer: React.FC<OUIAProps> = () => {
  const history = useHistory();
  const gatewayApi: FormsListGatewayApi = useFormsListGatewayApi();
  const appContext = useDevUIAppContext();

  useEffect(() => {
    const unsubscriber = gatewayApi.onOpenFormListen({
      onOpen(formData: FormInfo) {
        history.push({
          pathname: `/Forms/${formData.name}`,
          state: {
            filter: gatewayApi.getFormFilter(),
            formData: formData
          }
        });
      }
    });
    return () => {
      unsubscriber.unSubscribe();
    };
  }, []);

  return (
    <EmbeddedFormsList
      driver={gatewayApi}
      targetOrigin={appContext.getDevUIUrl()}
    />
  );
};

export default FormsListContainer;
