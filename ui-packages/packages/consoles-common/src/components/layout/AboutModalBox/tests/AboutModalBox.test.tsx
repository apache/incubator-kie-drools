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
import { render, screen } from '@testing-library/react';
import AboutModal from '../AboutModalBox';
import { BrandContext } from '../../BrandContext/BrandContext';

const props = {
  isOpenProp: true,
  handleModalToggleProp: jest.fn()
};
describe('AboutModal component tests', () => {
  it('snapshot testing', () => {
    process.env.KOGITO_APP_VERSION = '1.2.3-MOCKED-VERSION';
    const { container } = render(
      <BrandContext.Provider
        value={{
          imageSrc: 'kogito-image-src',
          altText: 'kogito image alt text'
        }}
      >
        <AboutModal {...props} />
      </BrandContext.Provider>
    );
    expect(container).toMatchSnapshot();
  });
});
