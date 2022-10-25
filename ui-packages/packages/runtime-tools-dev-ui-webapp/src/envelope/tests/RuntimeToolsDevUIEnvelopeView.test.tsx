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

import React from 'react';
import { act } from 'react-dom/test-utils';
import { mount } from 'enzyme';
import { RuntimeToolsDevUIEnvelopeView } from '../RuntimeToolsDevUIEnvelopeView';
import RuntimeTools from '../../components/DevUI/RuntimeTools/RuntimeTools';
import { RuntimeToolsDevUIEnvelopeViewApi } from '../RuntimeToolsDevUIEnvelopeViewApi';

jest.mock('../../components/DevUI/RuntimeTools/RuntimeTools');

describe('RuntimeToolsDevUIEnvelopeView tests', () => {
  it('Snapshot::Process and Tracing enabled', () => {
    const forwardRef = React.createRef<RuntimeToolsDevUIEnvelopeViewApi>();

    const wrapper = mount(
      <RuntimeToolsDevUIEnvelopeView ref={forwardRef} />
    ).find('RuntimeToolsDevUIEnvelopeView');

    expect(wrapper).toMatchSnapshot();

    act(() => {
      if (forwardRef.current) {
        forwardRef.current.setDataIndexUrl('http://localhost:4000');
        forwardRef.current.setTrustyServiceUrl('http://localhost:1336');
        forwardRef.current.setUsers([]);
        forwardRef.current.navigateTo('test');
        forwardRef.current.setDevUIUrl('http://localhost:8080');
        forwardRef.current.setOpenApiPath('/docs/openapi.json');
        forwardRef.current.setProcessEnabled(true);
        forwardRef.current.setTracingEnabled(true);
        forwardRef.current.setIsStunnerEnabled(false);
      }
    });
    const envelopeView = wrapper.update().find(RuntimeToolsDevUIEnvelopeView);

    expect(envelopeView).toMatchSnapshot();

    const devUI = envelopeView.find(RuntimeTools);

    expect(devUI.exists()).toBeTruthy();
  });

  it('Snapshot::Process enabled, Trusty disabled', () => {
    const forwardRef = React.createRef<RuntimeToolsDevUIEnvelopeViewApi>();

    const wrapper = mount(
      <RuntimeToolsDevUIEnvelopeView ref={forwardRef} />
    ).find('RuntimeToolsDevUIEnvelopeView');

    act(() => {
      if (forwardRef.current) {
        forwardRef.current.setProcessEnabled(true);
        forwardRef.current.setTracingEnabled(false);
        forwardRef.current.setDataIndexUrl('http://localhost:4000');
        forwardRef.current.setTrustyServiceUrl('http://localhost:8081');
        forwardRef.current.setUsers([]);
        forwardRef.current.navigateTo('test');
        forwardRef.current.setIsStunnerEnabled(false);
      }
    });
    const envelopeView = wrapper.update().find(RuntimeToolsDevUIEnvelopeView);

    expect(envelopeView).toMatchSnapshot();

    const devUI = envelopeView.find(RuntimeTools);

    expect(devUI.exists()).toBeTruthy();
  });

  it('Snapshot::Process disabled, Trusty enabled', () => {
    const forwardRef = React.createRef<RuntimeToolsDevUIEnvelopeViewApi>();

    const wrapper = mount(
      <RuntimeToolsDevUIEnvelopeView ref={forwardRef} />
    ).find('RuntimeToolsDevUIEnvelopeView');

    act(() => {
      if (forwardRef.current) {
        forwardRef.current.setProcessEnabled(false);
        forwardRef.current.setTracingEnabled(true);
        forwardRef.current.setDataIndexUrl('http://localhost:4000');
        forwardRef.current.setTrustyServiceUrl('http://localhost:8081');
        forwardRef.current.setUsers([]);
        forwardRef.current.navigateTo('test');
        forwardRef.current.setIsStunnerEnabled(false);
      }
    });
    const envelopeView = wrapper.update().find(RuntimeToolsDevUIEnvelopeView);

    expect(envelopeView).toMatchSnapshot();

    const devUI = envelopeView.find(RuntimeTools);

    expect(devUI.exists()).toBeTruthy();
  });

  it('Snapshot::Process disabled, Trusty disabled', () => {
    const forwardRef = React.createRef<RuntimeToolsDevUIEnvelopeViewApi>();

    const wrapper = mount(
      <RuntimeToolsDevUIEnvelopeView ref={forwardRef} />
    ).find('RuntimeToolsDevUIEnvelopeView');

    act(() => {
      if (forwardRef.current) {
        forwardRef.current.setProcessEnabled(false);
        forwardRef.current.setTracingEnabled(false);
        forwardRef.current.setDataIndexUrl('http://localhost:4000');
        forwardRef.current.setTrustyServiceUrl('http://localhost:8081');
        forwardRef.current.setUsers([]);
        forwardRef.current.navigateTo('test');
        forwardRef.current.setIsStunnerEnabled(false);
      }
    });
    const envelopeView = wrapper.update().find(RuntimeToolsDevUIEnvelopeView);

    expect(envelopeView).toMatchSnapshot();

    const devUI = envelopeView.find(RuntimeTools);

    expect(devUI.exists()).toBeFalsy();
  });

  it('Snapshot::Process enabled, Trusty enabled, navitageTo empty', () => {
    const forwardRef = React.createRef<RuntimeToolsDevUIEnvelopeViewApi>();

    const wrapper = mount(
      <RuntimeToolsDevUIEnvelopeView ref={forwardRef} />
    ).find('RuntimeToolsDevUIEnvelopeView');

    act(() => {
      if (forwardRef.current) {
        forwardRef.current.setProcessEnabled(true);
        forwardRef.current.setTracingEnabled(true);
        forwardRef.current.setDataIndexUrl('http://localhost:4000');
        forwardRef.current.setTrustyServiceUrl('http://localhost:8081');
        forwardRef.current.setUsers([]);
        forwardRef.current.navigateTo('');
        forwardRef.current.setIsStunnerEnabled(false);
      }
    });
    const envelopeView = wrapper.update().find(RuntimeToolsDevUIEnvelopeView);

    expect(envelopeView).toMatchSnapshot();

    const devUI = envelopeView.find(RuntimeTools);

    expect(devUI.exists()).toBeFalsy();
  });
});
