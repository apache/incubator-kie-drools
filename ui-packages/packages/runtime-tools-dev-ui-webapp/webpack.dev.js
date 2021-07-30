const path = require('path');
const merge = require('webpack-merge');
const common = require('./webpack.common.js');

const HOST = process.env.HOST || 'localhost';
const PORT = process.env.PORT || '9000';

module.exports = merge(common, {
  mode: 'development',
  devtool: 'source-map',
  devServer: {
    contentBase: './dist',
    host: HOST,
    port: PORT,
    compress: true,
    inline: true,
    historyApiFallback: true,
    hot: true,
    overlay: true,
    open: true,
    proxy: {
      '/svg': {
          target: 'http://localhost:4000',
          secure: false,
          changeOrigin: true
      },
    }
  },
  module: {
    rules: [
      {
        test: /\.css$/,
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
            '../../node_modules/@kogito-apps/consoles-common/dist/src/components/styles.css'
          ),
          path.resolve(
            '../../node_modules/@kogito-apps/components-common/dist/src/components/styles.css'
          ),
          path.resolve(
            '../../node_modules/@kogito-apps/jobs-management/dist/envelope/components/styles.css'
          ),
          path.resolve(
            '../../node_modules/@kogito-apps/process-details/dist/envelope/components/styles.css'
          ),
          path.resolve(
            '../../node_modules/@kogito-apps/management-console-shared/dist/src/components/styles.css'
          ),
          path.resolve(
            '../../node_modules/@kogito-apps/process-list/dist/envelope/components/styles.css'
            ),
          path.resolve(
            '../../node_modules/@kogito-apps/task-console-shared/dist/envelope/styles.css'
          ),
          path.resolve(
            '../../node_modules/@kogito-apps/task-form/dist/envelope/styles.css'
          ),
          path.resolve(
            '../../node_modules/react-calendar/dist/Calendar.css'
          ),
          path.resolve(
            '../../node_modules/react-clock/dist/Clock.css'
          ),
          path.resolve(
            '../../node_modules/react-datetime-picker/dist/DateTimePicker.css'
          )
        ],
        use: ['style-loader', 'css-loader']
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
});