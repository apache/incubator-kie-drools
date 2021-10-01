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

import React, { useState } from 'react';
import { Bullseye } from '@patternfly/react-core';
import { OUIAProps, componentOuiaProps } from '@kogito-apps/ouia-tools';
import { BallBeat } from 'react-pure-loaders';
import { FormArgs, FormInfo } from '../../../api';
import ReactFormRenderer from '../ReactFormRenderer/ReactFormRenderer';
import HtmlFormRenderer from '../HtmlFormRenderer/HtmlFormRenderer';
import '../styles.css';

interface FormDisplayerProps {
  isEnvelopeConnectedToChannel: boolean;
  content: FormArgs;
  config: FormInfo;
}

const FormDisplayer: React.FC<FormDisplayerProps & OUIAProps> = ({
  isEnvelopeConnectedToChannel,
  content,
  config,
  ouiaId,
  ouiaSafe
}) => {
  const [isExecuting, setIsExecuting] = useState<boolean>(false);

  return (
    <div {...componentOuiaProps(ouiaId, 'form-displayer', ouiaSafe)}>
      {isEnvelopeConnectedToChannel && !isExecuting ? (
        <>
          {config && config.type === 'TSX' ? (
            <ReactFormRenderer
              source={content.source['source-content']}
              resources={content.formConfiguration['resources']}
              setIsExecuting={setIsExecuting}
            />
          ) : (
            <HtmlFormRenderer
              source={content.source['source-content']}
              resources={content.formConfiguration['resources']}
            />
          )}
        </>
      ) : (
        <Bullseye className="kogito-form-displayer__ball-beats">
          <BallBeat color={'#000000'} loading={!isEnvelopeConnectedToChannel} />
        </Bullseye>
      )}
    </div>
  );
};

export default FormDisplayer;
