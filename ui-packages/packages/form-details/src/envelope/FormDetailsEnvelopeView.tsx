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
import React, { useImperativeHandle, useState } from 'react';
import { MessageBusClientApi } from '@kie-tools-core/envelope-bus/dist/api';
import { FormDetailsChannelApi } from '../api';
import FormDetails from './components/FormDetails/FormDetails';
import FormDetailsEnvelopeViewDriver from './FormDetailsEnvelopeViewDriver';
import '@patternfly/patternfly/patternfly.css';
import { FormInfo } from '@kogito-apps/components-common/dist/types';
import FormDetailsContextProvider from './components/contexts/FormDetailsContextProvider';

export interface FormDetailsEnvelopeViewApi {
  initialize: (formData?: FormInfo) => void;
}

interface Props {
  channelApi: MessageBusClientApi<FormDetailsChannelApi>;
  targetOrigin: string;
}

export const FormDetailsEnvelopeView = React.forwardRef<
  FormDetailsEnvelopeViewApi,
  Props
>((props, forwardedRef) => {
  const [isEnvelopeConnectedToChannel, setEnvelopeConnectedToChannel] =
    useState<boolean>(false);
  const [formData, setFormData] = useState<FormInfo>(null);
  useImperativeHandle(
    forwardedRef,
    () => ({
      initialize: (form: FormInfo) => {
        setFormData(form);
        setEnvelopeConnectedToChannel(true);
      }
    }),
    []
  );

  return (
    <React.Fragment>
      <FormDetailsContextProvider>
        <FormDetails
          isEnvelopeConnectedToChannel={isEnvelopeConnectedToChannel}
          driver={new FormDetailsEnvelopeViewDriver(props.channelApi)}
          formData={formData}
          targetOrigin={props.targetOrigin}
        />
      </FormDetailsContextProvider>
    </React.Fragment>
  );
});

export default FormDetailsEnvelopeView;
