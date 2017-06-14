const webpack = require('webpack')
const path = require('path')
const HtmlwebpackPlugin = require('html-webpack-plugin')

module.exports = {
  entry: './src/front_end/main.js',
  output: {
    path: path.resolve(__dirname, './build/front_end'),
    publicPath: '/',
    filename: 'bundle.js'
  },
  module: {
    loaders: [
      {
        test: path.join(__dirname, 'es6'),
        loader: 'babel-loader',
        query: {
          presets: ['es2015']
        }
      }
    ],
    rules: [
      {
        test: /\.vue$/,
        loader: 'vue-loader',
        options: {
          loaders: {
            // Since sass-loader (weirdly) has SCSS as its default parse mode, we map
            // the "scss" and "sass" values for the lang attribute to the right configs here.
            // other preprocessors should work out of the box, no loader config like this nessessary.
            'scss': 'vue-style-loader!css-loader!sass-loader',
            'sass': 'vue-style-loader!css-loader!sass-loader?indentedSyntax'
          }
          // other vue-loader options go here
        }
      },
      {
        test: /\.css$/,
        loader: 'style-loader!css-loader'
      },
      {
        test: /\.js$/,
        loader: 'babel-loader',
        exclude: /node_modules/
      },
      {
        test: /\.(png|jpg|gif|svg)$/,
        loader: 'file-loader',
        options: {
          name: '[name].[ext]?[hash]'
        }
      }
    ]
  },
  plugins: [
    new HtmlwebpackPlugin({
      title: 'CATPU Your Friend',
      favicon: './src/front_end/assets/images/favicon.ico',
      template: './src/front_end/index.html',
      inject: 'body',
      minify: {
        removeComments: true,
        collapseWhitespace: true
      }
    })
  ],
  resolve: {
    alias: {
      'vue$': 'vue/dist/vue.common.js'
    }
  },
  devServer: {
    historyApiFallback: true,
    noInfo: true,
    hot: true,
    inline: true,
    proxy: {
      '*': {
          target: 'http://localhost:3002',
          host: 'localhost',
          secure: false,
      }
    }
  }
}

if (process.env.NODE_ENV === 'production') {
  module.exports.devtool = '';
  // http://vue-loader.vuejs.org/en/workflow/production.html
  module.exports.plugins = (module.exports.plugins || []).concat([
    new webpack.DefinePlugin({
      'process.env': {
        NODE_ENV: '"production"'
      }
    }),
    new webpack.optimize.UglifyJsPlugin({
      sourceMap: true,
      compress: {
        warnings: false
      }
    }),
    new webpack.LoaderOptionsPlugin({
      minimize: true
    })
  ]);
}
