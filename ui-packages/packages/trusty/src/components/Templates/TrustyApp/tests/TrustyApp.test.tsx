import { mount } from 'enzyme';
import { MemoryRouter } from 'react-router';
import * as React from 'react';
import TrustyApp from '../TrustyApp';

describe('TrustyApp', () => {
  test('renders the not found page when a path is not matched', () => {
    const wrapper = mount(
      <MemoryRouter
        initialEntries={[
          {
            pathname: '/some-non-existent-page',
            key: 'some-none-existent-page'
          }
        ]}
      >
        <TrustyApp explanationEnabled={true} />
      </MemoryRouter>
    );

    expect(wrapper.find('NotFound')).toHaveLength(1);
  });
});
