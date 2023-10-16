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
import React from 'react';
import { mount } from 'enzyme';
import { Helmet } from 'react-helmet';
import ResourcesContainer from '../ResourcesContainer';
import { FormResources } from '../../../../api';

const getWrapper = (resources: FormResources) => {
  return mount(<ResourcesContainer resources={resources} />);
};

describe('ResourcesContainer tests', () => {
  it('Resources Rendering', () => {
    const resoures: FormResources = {
      styles: {
        style1: 'style1-url',
        style2: 'style2-url'
      },
      scripts: {
        script1: 'script1-url',
        script2: 'script2-url'
      }
    };

    const wrapper = getWrapper(resoures);

    expect(wrapper).toMatchSnapshot();

    const helmet = wrapper.find(Helmet);

    expect(helmet.exists()).toBeTruthy();

    const sideEffect = helmet.childAt(0);

    expect(sideEffect.exists()).toBeTruthy();

    const links = sideEffect.props().link;
    expect(links).toHaveLength(2);

    const link1 = links[0];
    expect(link1.href).toStrictEqual('style1-url');
    expect(link1.rel).toStrictEqual('stylesheet');

    const link2 = links[1];
    expect(link2.href).toStrictEqual('style2-url');
    expect(link2.rel).toStrictEqual('stylesheet');

    const scripts = sideEffect.props().script;
    expect(scripts).toHaveLength(2);

    const script1 = scripts[0];
    expect(script1.src).toStrictEqual('script1-url');

    const script2 = scripts[1];
    expect(script2.src).toStrictEqual('script2-url');
  });

  it('Empty Resources Rendering', () => {
    const resoures: FormResources = {
      styles: {},
      scripts: {}
    };

    const wrapper = getWrapper(resoures);

    expect(wrapper).toMatchSnapshot();

    const helmet = wrapper.find(Helmet);

    expect(helmet.exists()).toBeTruthy();

    const sideEffect = helmet.childAt(0);

    expect(sideEffect.exists()).toBeTruthy();

    expect(sideEffect.props().link).toBeUndefined();

    expect(sideEffect.props().script).toBeUndefined();
  });
});
