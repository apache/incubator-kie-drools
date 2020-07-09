import { ApolloProvider } from 'react-apollo-hooks';

jest.mock('apollo-link-http');

const renderMock = jest.fn();
jest.mock('react-dom', () => ({ render: renderMock }));

const rootDiv = document.createElement('div');
global.document.getElementById = id => id === 'root' && rootDiv;
process.env.KOGITO_DATAINDEX_HTTP_URL = 'http://localhost:8180';

describe('Index test', () => {
  it('regular rendering test', () => {
    require('../index.tsx');
    expect(renderMock).toBeCalled();
    expect(renderMock.mock.calls.length).toBe(1);

    const callArguments = renderMock.mock.calls[0];

    const context = callArguments[0];

    expect(context).not.toBeNull();
    expect(context).not.toBeInstanceOf(ApolloProvider);

    expect(callArguments[1]).toBe(rootDiv);
  });
});
