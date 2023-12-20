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
import React, { useEffect, useRef, useState } from 'react';
import {
  OUIAProps,
  componentOuiaProps
} from '@kogito-apps/ouia-tools/dist/utils/OuiaUtils';
import { FormDetailsDriver } from '../../../api/FormDetailsDriver';
import { Card } from '@patternfly/react-core/dist/js/components/Card';
import {
  Drawer,
  DrawerContent,
  DrawerContentBody,
  DrawerHead,
  DrawerPanelContent
} from '@patternfly/react-core/dist/js/components/Drawer';
import {
  Tab,
  Tabs,
  TabTitleText
} from '@patternfly/react-core/dist/js/components/Tabs';
import FormEditor from '../FormEditor/FormEditor';
import { KogitoSpinner } from '@kogito-apps/components-common/dist/components/KogitoSpinner';
import { ServerErrors } from '@kogito-apps/components-common/dist/components/ServerErrors';
import isEmpty from 'lodash/isEmpty';
import { Form, FormInfo } from '@kogito-apps/components-common/dist/types';
import FormDisplayerContainer from '../../containers/FormDisplayerContainer/FormDisplayerContainer';

export interface FormDetailsProps {
  isEnvelopeConnectedToChannel: boolean;
  driver: FormDetailsDriver;
  formData: FormInfo;
  targetOrigin: string;
}

export interface ResizableContent {
  doResize();
}

const FormDetails: React.FC<FormDetailsProps & OUIAProps> = ({
  isEnvelopeConnectedToChannel,
  driver,
  formData,
  ouiaId,
  ouiaSafe,
  targetOrigin
}) => {
  const [activeTab, setActiveTab] = useState<number>(0);
  const [formContent, setFormContent] = useState<Form>(null);
  const [error, setError] = useState<any>(null);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const editorResize = useRef<ResizableContent>();

  useEffect(() => {
    /* istanbul ignore else */
    if (isEnvelopeConnectedToChannel) {
      init();
    }
  }, [isEnvelopeConnectedToChannel]);

  const init = async (): Promise<void> => {
    try {
      /* istanbul ignore else */
      if (formData) {
        const response = await driver.getFormContent(formData.name);
        setFormContent(response);
        setIsLoading(false);
      }
    } catch (error) {
      setError(error);
    }
  };

  const saveForm = async (form: Form): Promise<void> => {
    try {
      setFormContent(form);
      await driver.saveFormContent(formData.name, {
        configuration: form.configuration,
        source: form.source
      });
    } catch (error) {
      setError(error);
    }
  };

  const panelContent: JSX.Element = (
    <DrawerPanelContent
      isResizable
      defaultSize={'800px'}
      onResize={() => {
        editorResize?.current.doResize();
      }}
    >
      <DrawerHead style={{ height: '100%' }}>
        {formContent && Object.keys(formContent)[0].length > 0 && (
          <span>
            <FormDisplayerContainer
              formContent={formContent}
              targetOrigin={targetOrigin}
            />
          </span>
        )}
      </DrawerHead>
    </DrawerPanelContent>
  );

  const onTabSelect = (event, tabIndex: number): void => {
    setActiveTab(tabIndex);
  };

  const getSource = (): string => {
    /* istanbul ignore else */
    if (!isEmpty(formContent)) {
      return formContent.source;
    }
  };
  const getType = (): string => {
    /* istanbul ignore else */
    if (!isEmpty(formData)) {
      return formData.type;
    }
  };
  const getConfig = (): string => {
    /* istanbul ignore else */
    if (!isEmpty(formContent)) {
      return JSON.stringify(formContent.configuration.resources, null, 2);
    }
  };
  if (error) {
    return <ServerErrors error={error} variant={'large'} />;
  }
  return (
    <div {...componentOuiaProps(ouiaId, 'form-details', ouiaSafe)}>
      {!isLoading ? (
        <Drawer isStatic>
          <DrawerContent panelContent={panelContent}>
            <Tabs isFilled activeKey={activeTab} onSelect={onTabSelect}>
              <Tab
                eventKey={0}
                title={<TabTitleText>Source</TabTitleText>}
                id="source-tab"
                aria-labelledby="source-tab"
              >
                <DrawerContentBody
                  style={{
                    padding: '0px',
                    background:
                      'var(--pf-c-page__main-section--BackgroundColor)'
                  }}
                >
                  {activeTab === 0 && (
                    <FormEditor
                      code={getSource()}
                      formContent={formContent}
                      setFormContent={setFormContent}
                      saveFormContent={saveForm}
                      isSource
                      formType={getType()}
                      ref={editorResize}
                    />
                  )}
                </DrawerContentBody>
              </Tab>
              <Tab
                eventKey={1}
                title={<TabTitleText>Connections</TabTitleText>}
                id="config-tab"
                aria-labelledby="config-tab"
              >
                <DrawerContentBody
                  style={{
                    padding: '0px',
                    background:
                      'var(--pf-c-page__main-section--BackgroundColor)'
                  }}
                >
                  {activeTab === 1 && (
                    <FormEditor
                      code={getConfig()}
                      formContent={formContent}
                      setFormContent={setFormContent}
                      saveFormContent={saveForm}
                      isConfig
                      ref={editorResize}
                    />
                  )}
                </DrawerContentBody>
              </Tab>
            </Tabs>
          </DrawerContent>
        </Drawer>
      ) : (
        <Card>
          <KogitoSpinner spinnerText="Loading form ..." />
        </Card>
      )}
    </div>
  );
};

export default FormDetails;
