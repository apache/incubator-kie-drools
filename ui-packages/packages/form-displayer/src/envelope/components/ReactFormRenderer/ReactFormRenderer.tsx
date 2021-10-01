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

import React, { useState, useEffect } from 'react';
import isEmpty from 'lodash/isEmpty';
import uuidv4 from 'uuid';
import * as Babel from '@babel/standalone';
import ReactDOM from 'react-dom';
import * as Patternfly from '@patternfly/react-core';
import { FormResources } from '../../../api';
import { renderResources, sourceHandler } from '../../../utils';
import Text = Patternfly.Text;
import TextContent = Patternfly.TextContent;
import TextVariants = Patternfly.TextVariants;
import '@patternfly/patternfly/patternfly.css';
interface ReactFormRendererProps {
  source: string;
  resources: FormResources;
  setIsExecuting: (isExecuting: boolean) => void;
}

const ReactFormRenderer: React.FC<ReactFormRendererProps> = ({
  source,
  resources,
  setIsExecuting
}) => {
  const [errorMessage, setErrorMessage] = useState<any>(null);

  useEffect(() => {
    /* istanbul ignore else */
    if (source) {
      renderform();
    }
  }, [source, resources]);

  const renderform = () => {
    /* istanbul ignore else */
    if (source) {
      setIsExecuting(true);
      try {
        window.React = React;
        window.ReactDOM = ReactDOM;

        // @ts-ignore
        window.PatternFlyReact = Patternfly;

        const container: HTMLElement = document.getElementById('formContainer');
        container.innerHTML = '';
        const id = uuidv4();
        const formContainer: HTMLElement = document.createElement('div');
        formContainer.id = id;

        container.appendChild(formContainer);
        renderResources('formContainer', resources);
        const {
          reactElements,
          patternflyElements,
          formName,
          trimmedSource
        } = sourceHandler(source);

        const scriptElement: HTMLScriptElement = document.createElement(
          'script'
        );

        // @ts-ignore
        window.PatternFly = window.PatternFlyReact;

        scriptElement.type = 'module';

        const content = `
        const {${reactElements}} = React;
        const {${patternflyElements}} = PatternFlyReact;
        ${trimmedSource}
        const target = document.getElementById("${id}");
        const element = window.React.createElement(${formName}, {});
        window.ReactDOM.render(element, target);
        `;

        const react = Babel.transform(content.trim(), {
          presets: [
            'react',
            [
              'typescript',
              {
                allExtensions: true,
                isTSX: true
              }
            ]
          ]
        }).code;
        scriptElement.text = react;

        container.appendChild(scriptElement);
        setIsExecuting(false);
      } catch (e) {
        setErrorMessage(e);
        setIsExecuting(false);
      }
    }
  };

  return (
    <>
      {isEmpty(errorMessage) ? (
        <div
          style={{
            height: '100%'
          }}
          id={'formContainer'}
        >
          {}
        </div>
      ) : (
        <>
          <TextContent>
            <Text component={TextVariants.h2} className="pf-u-danger-color-100">
              {errorMessage.name}
            </Text>
          </TextContent>
          <TextContent>
            <Text
              component={TextVariants.blockquote}
              className="pf-u-danger-color-100"
            >
              {errorMessage.message}
            </Text>
          </TextContent>
        </>
      )}
    </>
  );
};

export default ReactFormRenderer;
