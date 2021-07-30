const path = require('path');
const merge = require('webpack-merge');
const common = require('./webpack.common.js');
const MiniCssExtractPlugin = require('mini-css-extract-plugin');
const OptimizeCSSAssetsPlugin = require('optimize-css-assets-webpack-plugin');

module.exports = merge(common, {
  mode: 'production',
  devtool: 'source-map',
  optimization: {
    minimizer: [new OptimizeCSSAssetsPlugin({})]
  },
  plugins: [
    new MiniCssExtractPlugin({
      filename: '[name].css',
      chunkFilename: '[name].bundle.css'
    })
  ],
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
        loaders: ['style-loader', 'css-loader']
      }
    ]
  }
});