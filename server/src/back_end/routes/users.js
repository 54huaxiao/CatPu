/**
 * @Author: yuanxin
 * @Date:   2017-05-12
 * @Last modified by:   yx
 * @Last modified time: 2017-05-16
 */

const express = require('express')
const router = express.Router()
const user_controller = require('../controllers/user-controller')

router.post('/register', user_controller.register)
router.post('/login', user_controller.login)
//router.post('/logout', user_controller.logout)
router.post('/show', user_controller.show)
router.get('/info', user_controller.info)

module.exports = router
