const path = require('path');
const merge = require('webpack-merge');
const common = require('./webpack.common.js');
const webpack = require('webpack');

const HOST = process.env.HOST || 'localhost';
const PORT = process.env.PORT || '9000';

module.exports = function (env) {
  const dataIndexURL = env?.KOGITO_DATAINDEX_HTTP_URL ?? 'http://localhost:4000/graphql';
  return merge(common, {
    mode: 'development',
    devtool: 'source-map',
    devServer: {
      static: {
        directory: './dist'
      },
      host: HOST,
      port: PORT,
      compress: true,
      historyApiFallback: true,
      hot: true,
      open: true,
      client: {
        overlay: {
          warnings: false,
          errors: true
        },
        progress: true
      },
      proxy: {
        '/svg': {
          target: 'http://localhost:4000',
          secure: false,
          changeOrigin: true
        },
      }
    },
    plugins: [new webpack.EnvironmentPlugin({
      KOGITO_ENV_MODE: 'DEV',
      KOGITO_DATAINDEX_HTTP_URL: dataIndexURL
    })],
    module: {
      rules: [
        {
          test: /\.(css|sass|scss)$/,
          include: [
            path.resolve(__dirname, 'src'),
            path.resolve('../../node_modules/patternfly'),
            path.resolve('../../node_modules/@patternfly/patternfly'),
            path.resolve('../../node_modules/@patternfly/react-styles/css'),
            path.resolve(
              '../../node_modules/@patternfly/react-core/dist/styles/base.css'
            ),
            path.resolve(
              '../../node_modules/@patternfly/react-core/dist/esm/@patternfly/patternfly'
            ),
            path.resolve(
              '../../node_modules/@patternfly/react-core/node_modules/@patternfly/react-styles/css'
            ),
            path.resolve(
              '../../node_modules/@patternfly/react-table/node_modules/@patternfly/react-styles/css'
            ),
            path.resolve(
              '../../node_modules/@kogito-apps/consoles-common/dist/components/styles.css'
            ),
            path.resolve(
              '../../node_modules/@kogito-apps/components-common/dist/components/styles.css'
            ),
            path.resolve(
              '../../node_modules/@kogito-apps/jobs-management/dist/envelope/components/styles.css'
            ),
            path.resolve(
              '../../node_modules/@kogito-apps/process-details/dist/envelope/components/styles.css'
            ),
            path.resolve(
              '../../node_modules/@kogito-apps/management-console-shared/dist/components/styles.css'
            ),
            path.resolve(
              '../../node_modules/@kogito-apps/process-list/dist/envelope/components/styles.css'
            ),
            path.resolve(
              '../../node_modules/react-calendar/dist/Calendar.css'
            ),
            path.resolve(
              '../../node_modules/react-clock/dist/Clock.css'
            ),
            path.resolve(
              '../../node_modules/react-datetime-picker/dist/DateTimePicker.css'
            ),
            path.resolve(
              '../../node_modules/@kie-tools-core/guided-tour/dist/components'
            )
          ],
          use: ['style-loader', 'css-loader','sass-loader']
        }
      ]
    },
    resolve: {
      extensions: ['.tsx', '.ts', '.js', '.jsx'],
      modules: [
        path.resolve('../../node_modules'),
        path.resolve('./node_modules'),
        path.resolve('./src')
      ]
    }
  })
}