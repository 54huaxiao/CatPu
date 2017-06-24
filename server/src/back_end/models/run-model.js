/**
 * @Author: yx
 * @Date:   2017-06-23
 * @Email:  xieyx_pro@163.com
 * @Last modified by:   yx
 * @Last modified time: 2017-06-23
 */

const query = require('../utils/utils').queryDB

exports.retrieveData = (itemVal, item) => {
  let sql =
		'SELECT * \n' +
		'FROM RunTable \n' +
    'WHERE ' + item + ' = ? \n'
    ';'
  let vals = [itemVal]
  return query(sql, vals)
}

exports.insert = (cv) => {
	let sql = 
		'INSERT INTO RunTable(date, time, distance, username, _order) \n' +
		'VALUES(?, ?, ?, ?, ?) \n' +
		';'
		let vals = [cv.data, cv.time, cv.distance, cv.username, cv._order]
		return query(sql, vals)
}

exports.delete = (date) => {
  let sql = 
    'DELETE FROM RunTable where date = ?;'
  let vals = [date]
  return query(sql, vals)
}