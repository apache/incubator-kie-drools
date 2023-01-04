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
import { mount } from 'enzyme';
import DevUINav from '../DevUINav';
import { MemoryRouter } from 'react-router-dom';
import DevUIAppContextProvider from '../../../contexts/DevUIAppContextProvider';

describe('DevUINav tests::Process and Tracing enabled', () => {
  it('Snapshot testing with processes props', () => {
    const wrapper = mount(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={true}
        customLabels={{
          singularProcessLabel: 'Workflow',
          pluralProcessLabel: 'Workflows'
        }}
      >
        <MemoryRouter>
          <DevUINav pathname={'/Processes'} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    expect(wrapper.find('DevUINav')).toMatchSnapshot();

    const DevUINavWrapper = wrapper.findWhere(
      (nested) => nested.key() === 'processes-nav'
    );

    expect(DevUINavWrapper.exists()).toBeTruthy();
    expect(DevUINavWrapper.props().isActive).toBeTruthy();
  });

  it('Snapshot testing with jobs management props', () => {
    const wrapper = mount(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={true}
        customLabels={{
          singularProcessLabel: 'Workflow',
          pluralProcessLabel: 'Workflows'
        }}
      >
        <MemoryRouter>
          <DevUINav pathname={'/JobsManagement'} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    expect(wrapper.find('DevUINav')).toMatchSnapshot();

    const DevUINavWrapper = wrapper.findWhere(
      (nested) => nested.key() === 'jobs-management-nav'
    );

    expect(DevUINavWrapper.exists()).toBeTruthy();
    expect(DevUINavWrapper.props().isActive).toBeTruthy();
  });

  it('Snapshot testing with forms list props', () => {
    const wrapper = mount(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={true}
        customLabels={{
          singularProcessLabel: 'Workflow',
          pluralProcessLabel: 'Workflows'
        }}
      >
        <MemoryRouter>
          <DevUINav pathname={'/Forms'} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    expect(wrapper.find('DevUINav')).toMatchSnapshot();

    const DevUINavWrapper = wrapper.findWhere(
      (nested) => nested.key() === 'forms-list-nav'
    );

    expect(DevUINavWrapper.exists()).toBeTruthy();
    expect(DevUINavWrapper.props().isActive).toBeTruthy();
  });

  it('Snapshot testing audit investigation link props', () => {
    const wrapper = mount(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={true}
        customLabels={{
          singularProcessLabel: 'Workflow',
          pluralProcessLabel: 'Workflows'
        }}
      >
        <MemoryRouter>
          <DevUINav pathname={'/Audit'} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    expect(wrapper.find('DevUINav')).toMatchSnapshot();

    const DevUINavWrapper = wrapper.findWhere(
      (nested) => nested.key() === 'audit-nav'
    );

    expect(DevUINavWrapper.exists()).toBeTruthy();
    expect(DevUINavWrapper.props().isActive).toBeTruthy();
  });
});

describe('DevUINav tests::Sections disabled', () => {
  it('Snapshot testing with processes props', () => {
    const wrapper = mount(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={false}
        customLabels={{
          singularProcessLabel: 'Workflow',
          pluralProcessLabel: 'Workflows'
        }}
      >
        <MemoryRouter>
          <DevUINav pathname={'/Processes'} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    expect(wrapper.find('DevUINav')).toMatchSnapshot();

    expect(
      wrapper.exists('Link[data-ouia-navigation-name="processes-nav"]')
    ).toBeTruthy();
    expect(
      wrapper.exists('Link[data-ouia-navigation-name="jobs-management-nav"]')
    ).toBeTruthy();
    expect(
      wrapper.exists('Link[data-ouia-navigation-name="task-inbox-nav"]')
    ).toBeTruthy();
    expect(
      wrapper.exists('Link[data-ouia-navigation-name="forms-list-nav"]')
    ).toBeTruthy();
    expect(
      wrapper.exists('Link[data-ouia-navigation-name="audit-nav"]')
    ).toBeFalsy();
  });

  it('Snapshot testing with jobs management props', () => {
    const wrapper = mount(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={false}
        customLabels={{
          singularProcessLabel: 'Workflow',
          pluralProcessLabel: 'Workflows'
        }}
      >
        <MemoryRouter>
          <DevUINav pathname={'/JobsManagement'} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    expect(wrapper.find('DevUINav')).toMatchSnapshot();

    expect(
      wrapper.exists('Link[data-ouia-navigation-name="processes-nav"]')
    ).toBeTruthy();
    expect(
      wrapper.exists('Link[data-ouia-navigation-name="jobs-management-nav"]')
    ).toBeTruthy();
    expect(
      wrapper.exists('Link[data-ouia-navigation-name="task-inbox-nav"]')
    ).toBeTruthy();
    expect(
      wrapper.exists('Link[data-ouia-navigation-name="forms-list-nav"]')
    ).toBeTruthy();
    expect(
      wrapper.exists('Link[data-ouia-navigation-name="audit-nav"]')
    ).toBeFalsy();
  });

  it('Snapshot testing with forms list props', () => {
    const wrapper = mount(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={true}
        isTracingEnabled={false}
        customLabels={{
          singularProcessLabel: 'Workflow',
          pluralProcessLabel: 'Workflows'
        }}
      >
        <MemoryRouter>
          <DevUINav pathname={'/Forms'} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    expect(wrapper.find('DevUINav')).toMatchSnapshot();

    expect(
      wrapper.exists('Link[data-ouia-navigation-name="processes-nav"]')
    ).toBeTruthy();
    expect(
      wrapper.exists('Link[data-ouia-navigation-name="jobs-management-nav"]')
    ).toBeTruthy();
    expect(
      wrapper.exists('Link[data-ouia-navigation-name="task-inbox-nav"]')
    ).toBeTruthy();
    expect(
      wrapper.exists('Link[data-ouia-navigation-name="forms-list-nav"]')
    ).toBeTruthy();
    expect(
      wrapper.exists('Link[data-ouia-navigation-name="audit-nav"]')
    ).toBeFalsy();
  });

  it('Snapshot testing audit investigation link props', () => {
    const wrapper = mount(
      <DevUIAppContextProvider
        users={[]}
        devUIUrl="http://devUIUrl"
        openApiPath="http://openApiPath"
        isProcessEnabled={false}
        isTracingEnabled={true}
        customLabels={{
          singularProcessLabel: 'Workflow',
          pluralProcessLabel: 'Workflows'
        }}
      >
        <MemoryRouter>
          <DevUINav pathname={'/Audit'} />
        </MemoryRouter>
      </DevUIAppContextProvider>
    );

    expect(wrapper.find('DevUINav')).toMatchSnapshot();

    expect(
      wrapper.exists('Link[data-ouia-navigation-name="processes-nav"]')
    ).toBeFalsy();
    expect(
      wrapper.exists('Link[data-ouia-navigation-name="jobs-management-nav"]')
    ).toBeFalsy();
    expect(
      wrapper.exists('Link[data-ouia-navigation-name="task-inbox-nav"]')
    ).toBeFalsy();
    expect(
      wrapper.exists('Link[data-ouia-navigation-name="forms-list-nav"]')
    ).toBeFalsy();
    expect(
      wrapper.exists('Link[data-ouia-navigation-name="audit-nav"]')
    ).toBeTruthy();
  });
});
