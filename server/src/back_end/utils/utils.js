/**
 * @Author: zx
 * @Date:   2017-05-14
 * @Email:  yangzx8@mail2.sysu.edu.cn
 * @Last modified by:   zx
 * @Last modified time: 2017-05-14
 */

/**
 * 连接数据库
 * 使用缓冲池连接
 */

const mysql = require('mysql')
const Promise = require('bluebird')
const config = require('../config/config')
const conn = require('mysql/lib/Connection')
const po = require('mysql/lib/Pool')

Promise.promisifyAll(conn.prototype)
Promise.promisifyAll(po.prototype)

let getConnection = () => {
  return pool.getConnectionAsync()
    .disposer(connect => {
    	connect.release()
    })
}

let dbconfig = Object.assign({ connectionLimit: 10 }, config.db)
let pool = mysql.createPool(dbconfig)

exports.queryDB = (sql, vals) => {
  return Promise.using(getConnection(), connect => {
    return connect.queryAsync(sql, vals)
  })
}

exports.pool = pool