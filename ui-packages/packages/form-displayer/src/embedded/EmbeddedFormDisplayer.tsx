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

import React, { useCallback, useMemo } from 'react';
import {
  FormDisplayerApi,
  FormDisplayerChannelApi,
  FormDisplayerEnvelopeApi,
  FormArgs,
  FormInfo
} from '../api';
import { ContainerType } from '@kogito-tooling/envelope/dist/api';
import { EnvelopeServer } from '@kogito-tooling/envelope-bus/dist/channel';
import { EmbeddedEnvelopeFactory } from '@kogito-tooling/envelope/dist/embedded';

export type Props = {
  targetOrigin: string;
  formContent: FormArgs;
  formData: FormInfo;
  envelopePath: string;
};

export const EmbeddedFormDisplayer = React.forwardRef<FormDisplayerApi, Props>(
  (props, forwardedRef) => {
    const pollInit = useCallback((
      // eslint-disable-next-line
      envelopeServer: EnvelopeServer<
        FormDisplayerChannelApi,
        FormDisplayerEnvelopeApi
      >,
      container: () => HTMLElement
    ) => {
      return envelopeServer.envelopeApi.requests.formDisplayer__init(
        {
          origin: envelopeServer.origin,
          envelopeServerId: envelopeServer.id
        },
        {
          formContent: props.formContent,
          formData: props.formData
        }
      );
    }, []);

    const refDelegate = useCallback(
      (
        envelopeServer: EnvelopeServer<
          FormDisplayerChannelApi,
          FormDisplayerEnvelopeApi
        >
      ): FormDisplayerApi => ({
        formDisplayer__notify: formContent =>
          envelopeServer.envelopeApi.requests.formDisplayer__notify(formContent)
      }),
      []
    );

    const EmbeddedEnvelope = useMemo(() => {
      return EmbeddedEnvelopeFactory({
        api: props,
        origin: props.targetOrigin,
        refDelegate,
        pollInit,
        config: {
          containerType: ContainerType.IFRAME,
          envelopePath: props.envelopePath
        }
      });
    }, []);

    return <EmbeddedEnvelope ref={forwardedRef} />;
  }
);
