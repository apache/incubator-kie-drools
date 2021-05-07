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
import { shallow } from 'enzyme';
import ProcessDiagram from '../ProcessDiagram';

describe('ProcessDiagram component tests', () => {
  it('Snapshot testing  with default props', () => {
    const svg = {
      props: {
        src:
          '<svg version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" width="800" height="300" viewBox="0 0 1748 632"></svg>'
      }
    };
    const wrapper = shallow(<ProcessDiagram svg={svg} />);
    wrapper
      .find('ReactSvgPanZoomLoader')
      .props()
      ['render']();
    expect(wrapper).toMatchSnapshot();
  });
});
