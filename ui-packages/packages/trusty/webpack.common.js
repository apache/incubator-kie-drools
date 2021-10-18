const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const TsconfigPathsPlugin = require('tsconfig-paths-webpack-plugin');
const webpack = require('webpack');
const BG_IMAGES_DIRNAME = 'bgimages';

module.exports = {
  entry: {
    app: path.resolve(__dirname, 'src', 'index.tsx')
  },
  devServer: {
    contentBase: path.join(__dirname, 'static')
  },
  plugins: [
    new HtmlWebpackPlugin({
      template: path.resolve(__dirname, 'src', 'index.html'),
      favicon: 'src/favicon.ico'
    }),
    new webpack.EnvironmentPlugin({
      KOGITO_APP_VERSION: 'DEV',
      KOGITO_APP_NAME: 'Trusty',
      KOGITO_TRUSTY_API_HTTP_URL: 'http://localhost:1336'
    })
  ],
  module: {
    rules: [
      {
        test: /\.(tsx|ts)?$/,
        include: [path.resolve(__dirname, 'src')],
        use: [
          {
            loader: 'ts-loader',
            options: {
              configFile: path.resolve('./tsconfig.json'),
              allowTsInNodeModules: true,
              onlyCompileBundledFiles: true
            }
          }
        ]
      },
      {
        test: /\.(svg|ttf|eot|woff|woff2)$/,
        include: [/fonts|pficon/],
        use: {
          loader: 'file-loader',
          options: {
            // Limit at 50k. larger files emitted into separate files
            limit: 5000,
            outputPath: 'fonts',
            name: '[name].[ext]'
          }
        }
      },
      {
        test: /\.svg$/,
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
    plugins: [
      new TsconfigPathsPlugin({
        configFile: path.resolve(__dirname, './tsconfig.json')
      })
    ],
    symlinks: false,
    cacheWithContext: false
  }
};
