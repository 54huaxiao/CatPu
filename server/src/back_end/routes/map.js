/**
 * @Author: yuanxin
 * @Date:   2017-06-23
 * @Last modified by:   yx
 * @Last modified time: 2017-06-23
 */

const express = require('express')
const router = express.Router()
const map_controller = require('../controllers/map-controller')

router.post('/select', map_controller.select)
router.post('/insert', map_controller.insert)

module.exports = router
