const login_and_register = r => require.ensure([], () => r(require('./views/login_and_register.vue')), 'login_and_register_page')
const main_page = r => require.ensure([], () => r(require('./views/main_page.vue')), 'main_page')

module.exports = {
	history: false,
	routes: [{
		name: 'login_and_register_page',
		path: '/login_and_register_page',
		component: login_and_register
	},{
		name: 'main_page',
		path: '/main_page',
		component: main_page
	}]
}