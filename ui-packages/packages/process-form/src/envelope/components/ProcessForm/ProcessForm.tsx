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

import React, { useEffect, useState } from 'react';
import { componentOuiaProps, OUIAProps } from '@kogito-apps/ouia-tools';
import { ProcessDefinition, ProcessFormDriver } from '../../../api';
import {
  FormRenderer,
  KogitoSpinner,
  FormRendererApi,
  ServerErrors,
  FormAction
} from '@kogito-apps/components-common';
import { Bullseye } from '@patternfly/react-core/dist/js/layouts/Bullseye';
export interface ProcessFormProps {
  processDefinition: ProcessDefinition;
  driver: ProcessFormDriver;
  isEnvelopeConnectedToChannel: boolean;
}

const ProcessForm: React.FC<ProcessFormProps & OUIAProps> = ({
  processDefinition,
  driver,
  isEnvelopeConnectedToChannel,
  ouiaId,
  ouiaSafe
}) => {
  const formRendererApi = React.useRef<FormRendererApi>();
  const [processFormSchema, setProcessFormSchema] = useState<any>({});
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [error, setError] = useState<string>(null);

  const formAction: FormAction[] = [
    {
      name: 'Start'
    }
  ];

  useEffect(() => {
    if (!isEnvelopeConnectedToChannel) {
      setIsLoading(true);
      return;
    }
    init();
  }, [isEnvelopeConnectedToChannel]);

  const init = async (): Promise<void> => {
    try {
      const schema = await driver.getProcessFormSchema(processDefinition);
      setProcessFormSchema(schema);
    } catch (errorContent) {
      setError(errorContent);
    } finally {
      setIsLoading(false);
    }
  };

  const onSubmit = (value: any): void => {
    driver.startProcess(value).then(() => {
      formRendererApi?.current?.doReset();
    });
  };

  if (isLoading) {
    return (
      <Bullseye>
        <KogitoSpinner
          spinnerText="Loading process forms..."
          ouiaId="process-form-loading"
        />
      </Bullseye>
    );
  }

  if (error) {
    return <ServerErrors error={error} variant={'large'} />;
  }

  return (
    <div
      {...componentOuiaProps(
        ouiaId,
        'process-form',
        ouiaSafe ? ouiaSafe : !isLoading
      )}
    >
      <FormRenderer
        formSchema={processFormSchema}
        model={{}}
        readOnly={false}
        onSubmit={onSubmit}
        formActions={formAction}
        ref={formRendererApi}
      />
    </div>
  );
};

export default ProcessForm;
