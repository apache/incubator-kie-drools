import Keycloak from 'keycloak-js';
import { ApolloProvider } from 'react-apollo-hooks';

jest.mock('apollo-link-http');
jest.mock('keycloak-js');
const mockedKeycloak = Keycloak as jest.Mocked<typeof Keycloak>;

const MockKeycloakInstance = jest.fn(() => ({
  init: jest.fn()
}));

const MockKeycloakInstancePromise = jest.fn(() => ({
  success: jest.fn()
}));

const renderMock = jest.fn();
jest.mock('react-dom', () => ({ render: renderMock }));

const rootDiv = document.createElement('div');
global.document.getElementById = id => id === 'root' && rootDiv;
process.env.KOGITO_DATAINDEX_HTTP_URL = 'http://localhost:8180';

describe('Index test with Keycloak', () => {
  it('rendering with keycloak', () => {
    process.env.KOGITO_AUTH_ENABLED = 'true';
    process.env.KOGITO_KEYCLOAK_URL = 'http://localhost/keycloak';
    process.env.KOGITO_KEYCLOAK_CLIENT_ID = 'clientId';

    const instance = new MockKeycloakInstance();

    const mockPromise = new MockKeycloakInstancePromise();

    instance.init.mockReturnValue(mockPromise);

    // @ts-ignore
    mockedKeycloak.mockReturnValueOnce(instance);

    require('../index.tsx');

    expect(renderMock).not.toBeCalled();
    expect(renderMock.mock.calls.length).toBe(0);

    expect(mockPromise.success.mock.calls.length).toBe(1);

    const successCallback = mockPromise.success.mock.calls[0][0];

    successCallback(true);

    expect(renderMock).toBeCalled();
    expect(renderMock.mock.calls.length).toBe(1);

    const callArguments = renderMock.mock.calls[0];

    const context = callArguments[0];

    expect(context).not.toBeNull();
    expect(context).not.toBeInstanceOf(ApolloProvider);

    expect(callArguments[1]).toBe(rootDiv);
  });
});
