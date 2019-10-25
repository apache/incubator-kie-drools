const path = require('path');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const TsconfigPathsPlugin = require('tsconfig-paths-webpack-plugin');

const BG_IMAGES_DIRNAME = 'bgimages';

module.exports = {
  entry: {
    app: path.resolve(__dirname, 'src', 'index.tsx')
  },
  plugins: [
    new HtmlWebpackPlugin({
      template: path.resolve(__dirname, 'src', 'index.html')
    })
  ],
  module: {
    rules: [
      {
        test: /\.(tsx|ts)?$/,
        include: path.resolve(__dirname, 'src'),
        use: [
          {
            loader: 'ts-loader',
            options: {
              configFile: path.resolve("./tsconfig.json")
            }
          }
        ]
      },
      {
        test: /\.(svg|ttf|eot|woff|woff2)$/,
        // only process modules with this loader
        // if they live under a 'fonts' or 'pficon' directory
        include: [
          path.resolve('../../node_modules/patternfly/dist/fonts'),
          path.resolve('../../node_modules/@patternfly/react-core/dist/styles/assets/fonts'),
          path.resolve('../../node_modules/@patternfly/react-core/dist/styles/assets/pficon'),
          path.resolve('../../node_modules/@patternfly/patternfly/assets/fonts'),
          path.resolve('../../node_modules/@patternfly/patternfly/assets/pficon')
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
        // only process SVG modules with this loader if they live under a 'bgimages' directory
        // this is primarily useful when applying a CSS background using an SVG
        include: input => input.indexOf(BG_IMAGES_DIRNAME) > -1,
        use: {
          loader: 'svg-url-loader',
          options: {}
        }
      },
      {
        test: /\.svg$/,
        include: input =>
          input.indexOf(BG_IMAGES_DIRNAME) === -1 &&
          input.indexOf('fonts') === -1 &&
          input.indexOf('background-filter') === -1 &&
          input.indexOf('pficon') === -1,
        use: {
          loader: 'raw-loader',
          options: {}
        }
      },
      {
        test: /\.(jpg|jpeg|png|gif)$/i,
        include: [
          path.resolve(__dirname, 'src'),
          path.resolve('../../node_modules/patternfly'),
          path.resolve('../../node_modules/@patternfly/patternfly/assets/images'),
          path.resolve('../../node_modules/@patternfly/react-styles/css/assets/images'),
          path.resolve('../../node_modules/@patternfly/react-core/dist/styles/assets/images'),
          path.resolve(
            '../../node_modules/@patternfly/react-core/node_modules/@patternfly/react-styles/css/assets/images'
          ),
          path.resolve(
            '../../node_modules/@patternfly/react-table/node_modules/@patternfly/react-styles/css/assets/images'
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
    path: path.resolve(__dirname, 'dist')
  },
  resolve: {
    extensions: ['.ts', '.tsx', '.js'],
    modules: [path.resolve('../../node_modules'), path.resolve('./node_modules'), path.resolve('./src')],
    plugins: [
      new TsconfigPathsPlugin({
        configFile: path.resolve(__dirname, './tsconfig.json')
      })
    ],
    symlinks: false,
    cacheWithContext: false
  }
};
