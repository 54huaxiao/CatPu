/**
 * 后端路径配置等
 * routes路径实现路由，controllers路径实现业务逻辑，models路径实现数据交互
 */

const express = require('express')
const app = express()

//***********************下面为第三方中间件*****************************
const path = require('path')
const favicon = require('serve-favicon')
const logger = require('morgan')
const cookieParser = require('cookie-parser')
const bodyParser = require('body-parser')
const session = require('express-session')
const MySqlStore = require('connect-mysql')(session)
const pool = require('./utils/utils').pool

//***********************下面为路由级中间件******************************
const users = require('./routes/users')
const map = require('./routes/map')
const run = require('./routes/run')

//***********************下面为自定义模块********************************
const config = require('./config/config')



// view engine setup
app.set('views', path.join(__dirname + '/../../src/front_end', 'views'))
app.set('view engine', 'jade')

//***********************下面为中间件的配置******************************
app.use(logger('dev'))
app.use(bodyParser.json())
app.use(bodyParser.urlencoded({ extended: false }))
app.use(cookieParser())
const sc = config.session_config
sc.store = new MySqlStore({ pool })
app.use(session(sc))

//前端资源路径
app.use(express.static(__dirname + '/../front_end'))

//路由配置
app.use('/api/user', users)
app.use('/api/map', map)
app.use('/api/run', run)

//错误抛出404异常
app.use(function(req, res, next) {
  var err = new Error('Not Found')
  err.status = 404
  next(err)
})

//异常处理
app.use(function(err, req, res, next) {
  //仅开发环境抛出异常
  res.locals.message = err.message
  res.locals.error = req.app.get('env') === 'development' ? err : {}

  //错误页面
  res.status(err.status || 500)
  res.send('error')
})

module.exports = app