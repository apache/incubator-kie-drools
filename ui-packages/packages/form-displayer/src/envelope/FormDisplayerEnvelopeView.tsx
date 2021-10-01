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

import * as React from 'react';
import { useImperativeHandle, useState } from 'react';
import isEmpty from 'lodash/isEmpty';
import { FormDisplayerChannelApi, FormArgs, FormInfo } from '../api';
import { MessageBusClientApi } from '@kogito-tooling/envelope-bus/dist/api';
import FormDisplayer from './components/FormDisplayer/FormDisplayer';

export interface FormDisplayerEnvelopeViewApi {
  setFormContent: (formContent: FormArgs, formData: FormInfo) => void;
  notify: (userName: FormArgs) => Promise<void>;
}

interface Props {
  channelApi: MessageBusClientApi<FormDisplayerChannelApi>;
}

export const FormDisplayerEnvelopeView = React.forwardRef<
  FormDisplayerEnvelopeViewApi,
  Props
>((props, forwardedRef) => {
  const [content, setContent] = useState<FormArgs>();
  const [config, setConfig] = useState<FormInfo>();
  const [
    isEnvelopeConnectedToChannel,
    setEnvelopeConnectedToChannel
  ] = useState<boolean>(false);

  useImperativeHandle(
    forwardedRef,
    () => {
      return {
        setFormContent: (formContent: FormArgs, formData: FormInfo) => {
          setContent(formContent);
          setConfig(formData);
          setEnvelopeConnectedToChannel(true);
        },
        notify: (formContent: FormArgs) => {
          if (!isEmpty(formContent)) {
            setEnvelopeConnectedToChannel(false);
            setContent(formContent);
            setEnvelopeConnectedToChannel(true);
          }
          return Promise.resolve();
        }
      };
    },
    []
  );

  return (
    <FormDisplayer
      isEnvelopeConnectedToChannel={isEnvelopeConnectedToChannel}
      content={content}
      config={config}
    />
  );
});
