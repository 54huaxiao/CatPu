/**
 * @Author: yuanxin
 * @Date:   2017-06-23
 * @Last modified by:   yx
 * @Last modified time: 2017-06-23
 */

const express = require('express')
const router = express.Router()
const run_controller = require('../controllers/run-controller')

router.post('/select', run_controller.select)
router.post('/insert', run_controller.insert)
router.post('/delete', run_controller.delete)

module.exports = router
