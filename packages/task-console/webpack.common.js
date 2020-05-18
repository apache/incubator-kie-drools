const path = require('path');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const TsconfigPathsPlugin = require('tsconfig-paths-webpack-plugin');

const BG_IMAGES_DIRNAME = 'bgimages';

module.exports = {
  entry: {
    app: path.resolve(__dirname, 'src', 'index.tsx')
  },
  module: {
    rules: [
      {
        include: [
          path.resolve(__dirname, 'src'),
          path.resolve('../../node_modules/@kogito-apps/common/src/components')
        ],
        test: /\.(tsx|ts)?$/,
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
        // only process modules with this loader
        // if they live under a 'fonts' or 'pficon' directory
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
          path.resolve('./src/static'),
          path.resolve('../../node_modules/@kogito-apps/common/src/static')
        ],
        test: /\.(svg|ttf|eot|woff|woff2)$/,
        use: {
          loader: 'file-loader',
          options: {
            // Limit at 50k. larger files emited into separate files
            limit: 5000,
            name: '[name].[ext]',
            outputPath: 'fonts'
          }
        }
      },
      {
        include: input => input.indexOf('background-filter.svg') > 1,
        test: /\.svg$/,
        use: [
          {
            loader: 'url-loader',
            options: {
              limit: 5000,
              name: '[name].[ext]',
              outputPath: 'svgs'
            }
          }
        ]
      },
      {
        // only process SVG modules with this loader if they live under a 'bgimages' directory
        // this is primarily useful when applying a CSS background using an SVG
        include: input => input.indexOf(BG_IMAGES_DIRNAME) > -1,
        test: /\.svg$/,
        use: {
          loader: 'svg-url-loader',
          options: {}
        }
      },
      {
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
            __dirname,
            '../../node_modules/@kogito-apps/common/src/static'
          )
        ],
        test: /\.(jpg|jpeg|png|gif)$/i,
        use: [
          {
            loader: 'url-loader',
            options: {
              limit: 5000,
              name: '[name].[ext]',
              outputPath: 'images'
            }
          }
        ]
      }
    ]
  },
  output: {
    filename: '[name].bundle.js',
    path: path.resolve(__dirname, 'dist')
  },
  plugins: [
    new HtmlWebpackPlugin({
      favicon: 'src/favicon.ico',
      template: path.resolve(__dirname, 'src', 'index.html')
    }),
    new webpack.EnvironmentPlugin({
      KOGITO_AUTH_ENABLED: false,
      KOGITO_KEYCLOAK_REALM: 'kogito',
      KOGITO_KEYCLOAK_URL: 'http://localhost:8280',
      KOGITO_KEYCLOAK_CLIENT_ID: 'kogito-task-console',
      KOGITO_DATAINDEX_HTTP_URL: 'http://localhost:4000/graphql',
      KOGITO_APP_VERSION: 'DEV',
      KOGITO_APP_NAME: 'Task Console'
    })
  ],
  resolve: {
    cacheWithContext: false,
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
    symlinks: false
  }
};
