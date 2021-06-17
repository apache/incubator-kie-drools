import React from 'react';
import { MemoryRouter as Router } from 'react-router-dom';
import { mount } from 'enzyme';
import PageLayout from '../PageLayout';
import taskConsoleLogo from '../../../../static/taskConsoleLogo.svg';

const props: any = {
  location: {
    pathname: '/'
  },
  history: []
};

const MockedComponent = (): React.ReactElement => {
  return <></>;
};

jest.mock('@kogito-apps/common', () => ({
  ...jest.requireActual('@kogito-apps/common'),
  KogitoPageLayout: () => {
    return <MockedComponent />;
  }
}));

function testRoute(route: string) {
  props.location.pathname = route;

  const wrapper = mount(
    <Router keyLength={0}>
      <PageLayout {...props} />
    </Router>
  ).find('PageLayout');

  expect(wrapper).toMatchSnapshot();

  const mockedKogitoPageLayout = wrapper.find('KogitoPageLayout').getElement();

  expect(mockedKogitoPageLayout).not.toBeNull();

  expect(mockedKogitoPageLayout.props.BrandAltText).toBe('Task Console Logo');
  expect(mockedKogitoPageLayout.props.BrandSrc).toBe(taskConsoleLogo);
  expect(mockedKogitoPageLayout.props.PageNav).not.toBeNull();
  expect(mockedKogitoPageLayout.props.BrandClick).not.toBeNull();
}

describe('PageLayout tests', () => {
  it('test default route', () => {
    testRoute('/');
  });

  it('test TaskInbox route', () => {
    testRoute('/TaskInbox');
  });

  it('test TaskDetails route', () => {
    testRoute('/TaskDetails/taskId');
  });
});
