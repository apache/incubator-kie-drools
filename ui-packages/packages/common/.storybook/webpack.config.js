const path = require("path");
const SRC_PATH = path.join(__dirname, '../src');
const STORIES_PATH = path.join(__dirname, '../stories');
const webpack = require('webpack');

module.exports = ({config}) => {
  config.plugins.push(
    new webpack.EnvironmentPlugin({
      KOGITO_DATAINDEX_HTTP_URL: 'http://localhost:4000/graphql',
      KOGITO_APP_VERSION: 'DEV',
      KOGITO_APP_NAME: 'Kogito Console'
    })
  )
  config.module.rules.push({
    test: /\.(ts|tsx)$/,
    include: [SRC_PATH, STORIES_PATH],
      use: [
        {
          loader: require.resolve('ts-loader'),
          options: {
            configFile: path.resolve('./tsconfig.json')
          }
        },
        { loader: require.resolve('react-docgen-typescript-loader') }
      ]
  },
  {
    test: /\.css$/,
    include: [
      path.resolve('../src'),
      path.resolve('../../../node_modules/patternfly'),
      path.resolve('../../../node_modules/@patternfly/patternfly'),
      path.resolve('../../../node_modules/@patternfly/react-styles/css'),
      path.resolve(
        '../../../node_modules/@patternfly/react-core/dist/styles/base.css'
      ),
      path.resolve(
        '../../../node_modules/@patternfly/react-core/dist/esm/@patternfly/patternfly'
      ),
      path.resolve(
        '../../../node_modules/@patternfly/react-core/node_modules/@patternfly/react-styles/css'
      ),
      path.resolve(
        '../../../node_modules/@patternfly/react-table/node_modules/@patternfly/react-styles/css'
      ),
      path.resolve (
        '../../../node_modules/@kogito-apps/common/src/components'
      )
    ],
    use: ['style-loader', 'css-loader']
  });

  config.resolve.extensions.push('.ts', '.tsx', '.js', '.jsx');
  config.resolve.modules.push(
    path.resolve('../../../node_modules'),
      path.resolve('./node_modules'),
      path.resolve('../src')
  )
  config.module.rules.push({
    test: /\.stories\.tsx?$/,
    loaders: [
      {
        loader: require.resolve('@storybook/source-loader'),
        options: { parser: 'typescript' },
      },
    ],
    enforce: 'pre',
  });

  config.module.rules.push({
    test: /\.(svg|ttf|eot|woff|woff2)$/,
    include: [
      path.resolve('../src/static'),
      path.resolve('../../../node_modules/@kogito-apps/management-console/src/static'),
      path.resolve('../../../node_modules/@kogito-apps/task-console/src/static')
    ],
    use: {
      loader: 'file-loader',
      options: {
        // Limit at 50k. larger files emited into separate files
        limit: 5000,
        name: '[name].[ext]',
        outputPath: 'fonts'
      }
    }
    })
  return config;
} 