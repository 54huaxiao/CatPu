/**
 * @Author: yuanxin
 * @Date:   2017-05-12
 * @Last modified by:   zx
 * @Last modified time: 2017-05-15
 * @Email:  yangzx8@mail2.sysu.edu.cn
 */

const express = require('express')
const router = express.Router()
const user_controller = require('../controllers/user-controller')

router.post('/signup', user_controller.signup)
router.post('/login', user_controller.login)
router.post('/logout', user_controller.logout)
router.post('/show', user_controller.show)
router.get('/info', user_controller.info)

module.exports = router
