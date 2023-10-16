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
import { shallow } from 'enzyme';
import SkeletonFlexStripes from '../SkeletonFlexStripes';

describe('SkeletonFlexStripes', () => {
  test('renders a list of three stripes', () => {
    const wrapper = shallow(
      <SkeletonFlexStripes
        stripesNumber={3}
        stripesWidth="200px"
        stripesHeight="1em"
      />
    );

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.prop('className')).toMatch('skeleton__flex-stripes');
    expect(wrapper.find('SkeletonStripe')).toHaveLength(3);
    expect(
      wrapper.find('SkeletonStripe').at(0).prop('customStyle')
    ).toStrictEqual({
      width: '200px',
      height: '1em'
    });
  });

  test('renders a padded list of stripes', () => {
    const wrapper = shallow(
      <SkeletonFlexStripes
        stripesNumber={2}
        stripesWidth="200px"
        stripesHeight="1em"
        isPadded
      />
    );

    expect(wrapper).toMatchSnapshot();
    expect(wrapper.prop('className')).toMatch(
      'skeleton__flex-stripes skeleton__flex-stripes--padded'
    );
  });
});
