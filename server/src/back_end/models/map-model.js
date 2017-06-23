/**
 * @Author: yx
 * @Date:   2017-06-23
 * @Email:  xieyx_pro@163.com
 * @Last modified by:   yx
 * @Last modified time: 2017-06-23
 */

const query = require('../utils/utils').queryDB

exports.retrieveData = () => {
  let sql =
		'SELECT * \n' +
		'FROM MapTable \n' +
    ';'
  return query(sql, [])
}

exports.insert = (map) => {
	let sql = 
		'INSERT INTO MapTable(_order, latitude, longitude) \n' +
		'VALUES(?, ?, ?) \n' +
		';'
		let vals = [map._order, map.latitude, map.longitude]
		return query(sql, vals)
}