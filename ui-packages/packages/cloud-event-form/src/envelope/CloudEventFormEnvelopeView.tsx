/*
 * Copyright 2023 Red Hat, Inc. and/or its affiliates.
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

import React, {
  useEffect,
  useImperativeHandle,
  useMemo,
  useState
} from 'react';
import { Bullseye } from '@patternfly/react-core/dist/js/layouts/Bullseye';
import {
  componentOuiaProps,
  OUIAProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import { KogitoSpinner } from '@kogito-apps/components-common/dist/components/KogitoSpinner';
import { MessageBusClientApi } from '@kie-tools-core/envelope-bus/dist/api';
import {
  CloudEventFormChannelApi,
  CloudEventFormDefaultValues,
  CloudEventFormInitArgs
} from '../api';
import '@patternfly/patternfly/patternfly.css';
import CloudEventForm from './components/CloudEventForm/CloudEventForm';
import { CloudEventFormEnvelopeViewDriver } from './CloudEventFormEnvelopeViewDriver';

export interface CloudEventFormEnvelopeViewApi {
  initialize: (args: CloudEventFormInitArgs) => void;
}

interface Props {
  channelApi: MessageBusClientApi<CloudEventFormChannelApi>;
}

export const CloudEventFormEnvelopeView = React.forwardRef<
  CloudEventFormEnvelopeViewApi,
  Props & OUIAProps
>(({ channelApi, ouiaId }, forwardedRef) => {
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [isEnvelopeConnectedToChannel, setEnvelopeConnectedToChannel] =
    useState<boolean>(false);
  const [isNewInstanceEvent, setIsNewInstanceEvent] = useState<boolean>(false);
  const [defaultValues, setDefaultValues] =
    useState<CloudEventFormDefaultValues>();

  useImperativeHandle(
    forwardedRef,
    () => ({
      initialize: (args) => {
        setEnvelopeConnectedToChannel(true);
        setIsNewInstanceEvent(args.isNewInstanceEvent);
        setDefaultValues(args.defaultValues);
      }
    }),
    []
  );

  useEffect(() => {
    setIsLoading(false);
  }, [isEnvelopeConnectedToChannel]);

  const driver = useMemo(
    () => new CloudEventFormEnvelopeViewDriver(channelApi),
    [channelApi]
  );

  if (isLoading) {
    return (
      <Bullseye
        {...componentOuiaProps(
          /* istanbul ignore next */
          (ouiaId ? ouiaId : 'cloud-event-form-envelope-view') +
            '-loading-spinner',
          'cloud-event-form',
          true
        )}
      >
        <KogitoSpinner spinnerText={`Loading cloud event form...`} />
      </Bullseye>
    );
  }

  return (
    <CloudEventForm
      driver={driver}
      isNewInstanceEvent={isNewInstanceEvent}
      defaultValues={defaultValues}
    />
  );
});

export default CloudEventFormEnvelopeView;
