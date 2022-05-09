
const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const TsconfigPathsPlugin = require('tsconfig-paths-webpack-plugin');
const webpack = require('webpack');
const BG_IMAGES_DIRNAME = 'bgimages';

module.exports = {
  entry: {
    app: path.resolve(__dirname, 'src', 'index.tsx')
  },
  plugins: [
    new HtmlWebpackPlugin({
      template: path.resolve(__dirname, 'src', 'index.html'),
      favicon: 'src/favicon.ico',
      chunks: ['app']
    }),
    new webpack.EnvironmentPlugin({
      KOGITO_APP_VERSION: 'DEV',
      KOGITO_APP_NAME: 'Management Console'
    })
  ],
  module: {
    rules: [
      {
        test: /\.(tsx|ts)?$/,
        include: [
          path.resolve(__dirname, 'src')
        ],
        use: [
          {
            loader: 'ts-loader',
            options: {
              configFile: path.resolve('./tsconfig.json'),
              allowTsInNodeModules: true
            }
          }
        ]
      },
      {
        test: /\.(svg|ttf|eot|woff|woff2)$/,
        include: [
          path.resolve('../../node_modules/patternfly/dist/fonts'),
          path.resolve(
            '../../node_modules/@patternfly/react-core/dist/styles/assets/fonts'
          ),
          path.resolve(
            '../../node_modules/@patternfly/react-core/dist/styles/assets/pficon'
          ),
          path.resolve(
            '../../node_modules/@patternfly/patternfly/assets/fonts'
          ),
          path.resolve(
            '../../node_modules/@patternfly/patternfly/assets/pficon'
          ),
          path.resolve('./src/static/'),
          path.resolve(
            '../../node_modules/@kogito-apps/consoles-common/dist/static'
          ),
          path.resolve(
            '../../node_modules/@kogito-apps/components-common/dist/static'
          ),
          path.resolve(
            '../../node_modules/@kogito-apps/jobs-management/dist/static'
          ),
          path.resolve(
            '../../node_modules/@kogito-apps/process-details/dist/static'
          ),
          path.resolve(
            '../../node_modules/@kogito-apps/management-console-shared/dist/static'
          ),
          path.resolve(
            '../../node_modules/@kogito-apps/components-common/dist/static'
          ),
          path.resolve(
            '../../node_modules/@kogito-apps/process-list/dist/static'
          )
        ],
        use: {
          loader: 'file-loader',
          options: {
            // Limit at 50k. larger files emited into separate files
            limit: 5000,
            outputPath: 'fonts',
            name: '[name].[ext]'
          }
        }
      },
      {
        test: /\.svg$/,
        include: input => input.indexOf('background-filter.svg') > 1,
        use: [
          {
            loader: 'url-loader',
            options: {
              limit: 5000,
              outputPath: 'svgs',
              name: '[name].[ext]'
            }
          }
        ]
      },
      {
        test: /\.svg$/,
        include: input => input.indexOf(BG_IMAGES_DIRNAME) > -1,
        use: {
          loader: 'svg-url-loader',
          options: {}
        }
      },
      {
        test: /\.(jpg|jpeg|png|gif)$/i,
        include: [
          path.resolve(__dirname, 'src'),
          path.resolve('../../node_modules/patternfly'),
          path.resolve(
            '../../node_modules/@patternfly/patternfly/assets/images'
          ),
          path.resolve(
            '../../node_modules/@patternfly/react-styles/css/assets/images'
          ),
          path.resolve(
            '../../node_modules/@patternfly/react-core/dist/styles/assets/images'
          ),
          path.resolve(
            '../../node_modules/@patternfly/react-core/node_modules/@patternfly/react-styles/css/assets/images'
          ),
          path.resolve(
            '../../node_modules/@patternfly/react-table/node_modules/@patternfly/react-styles/css/assets/images'
          ),
          path.resolve(
            '../../node_modules/@kogito-apps/consoles-common/dist/static'
          ),
          path.resolve(
            '../../node_modules/@kogito-apps/components-common/dist/static'
          ),
          path.resolve(
            '../../node_modules/@kogito-apps/jobs-management/dist/static'
          ),
          path.resolve(
            '../../node_modules/@kogito-apps/process-details/dist/static'
          ),
          path.resolve(
            '../../node_modules/@kogito-apps/management-console-shared/dist/static'
          ),
          path.resolve(
            '../../node_modules/@kogito-apps/process-list/dist/static'
          )
        ],
        use: [
          {
            loader: 'url-loader',
            options: {
              limit: 5000,
              outputPath: 'images',
              name: '[name].[ext]'
            }
          }
        ]
      }
    ]
  },
  output: {
    filename: '[name].bundle.js',
    path: path.resolve(__dirname, 'dist'),
    publicPath: '/'
  },
  resolve: {
    extensions: ['.ts', '.tsx', '.js'],
    modules: [
      path.resolve('../../node_modules'),
      path.resolve('./node_modules'),
      path.resolve('./src')
    ],
    plugins: [
      new TsconfigPathsPlugin({
        configFile: path.resolve(__dirname, './tsconfig.json')
      })
    ],
    symlinks: false,
    cacheWithContext: false
  }
};