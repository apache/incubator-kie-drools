const path = require('path');
const merge = require('webpack-merge');
const common = require('./webpack.common.js');
const HtmlWebpackPlugin = require("html-webpack-plugin");

const HOST = process.env.HOST || 'localhost';
const PORT = process.env.PORT || '9000';

module.exports = merge(common, {
  mode: 'development',
  devtool: 'source-map',
  output: {
    publicPath: "/"
  },
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
    proxy: [
      {
        context: ['/svg', '/forms', '/customDashboard'],
        target: 'http://localhost:4000',
        secure: false,
        changeOrigin: true
      }
    ]
  },
  plugins: [
    new HtmlWebpackPlugin({
      template: path.resolve(__dirname, 'resources', 'index.html'),
      favicon: 'src/favicon.ico',
      chunks: ['app']
    })
  ],
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
            '../../node_modules/@kogito-apps/task-console-shared/dist/envelope/styles.css'
          ),
          path.resolve(
            '../../node_modules/@kogito-apps/task-form/dist/envelope/styles.css'
          ),
          path.resolve(
            '../../node_modules/@kogito-apps/form-details/dist/envelope/components/styles.css'
          ),
          path.resolve(
            '../../node_modules/@kogito-apps/form-displayer/dist/envelope/components/styles.css'
          ),
          path.resolve(
            '../../node_modules/@kogito-apps/process-form/dist/envelope/styles.css'
          ),
          path.resolve(
            '../../node_modules/@kogito-apps/workflow-form/dist/envelope/styles.css'
          ),
          path.resolve(
            '../../node_modules/@kogito-apps/process-definition-list/dist/envelope/styles.css'
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
            '../../node_modules/@kogito-apps/form-details/dist/styles/styles.css'
          ),
          path.resolve(
            '../../node_modules/@kogito-apps/workflow-form/dist/envelope/styles.css'
          ),
          path.resolve(
              '../../node_modules/@kogito-apps/cloud-event-form/dist/envelope/styles.css'
          ),
          path.resolve(
            '../../node_modules/@kie-tools-core/guided-tour/dist/components'
          ),
          path.resolve(
            '../../node_modules/@kie-tools-core/editor/dist/envelope'
          ),
          path.resolve(
            '../../node_modules/@kie-tools/serverless-workflow-mermaid-viewer/dist/viewer'
          )
        ],
        use: ['style-loader', 'css-loader', 'sass-loader']
      },
      {
        test: /\.css$/,
        include: [
          path.resolve(
            '../../node_modules/monaco-editor'
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