<template>
	<div id='main-page'>
		<div id='main-page-container'>
			<div id='date1' class='date'>
				<div class='date-paper-container'>
					<div v-bind:style='{top:step_num_tops[0]}' class='steps-num'>{{ steps[0] }}</div>
					<mu-paper v-bind:style='{height:heights[0]}' class='date-paper'></mu-paper>
				</div>
				<div class='date-text'>{{ dates[0] }}</div>
			</div>
			<div id='date2' class='date'>
				<div class='date-paper-container'>
					<div v-bind:style='{top:step_num_tops[1]}' class='steps-num'>{{ steps[1] }}</div>
					<mu-paper v-bind:style='{height:heights[1]}' class='date-paper'></mu-paper>
				</div>
				<div class='date-text'>{{ dates[1] }}</div>
			</div>
			<div id='date3' class='date'>
				<div class='date-paper-container'>
					<div v-bind:style='{top:step_num_tops[2]}' class='steps-num'>{{ steps[2] }}</div>
					<mu-paper v-bind:style='{height:heights[2]}' class='date-paper'></mu-paper>
				</div>
				<div class='date-text'>{{ dates[2] }}</div>
			</div>
			<div id='date4' class='date'>
				<div class='date-paper-container'>
					<div v-bind:style='{top:step_num_tops[3]}' class='steps-num'>{{ steps[3] }}</div>
					<mu-paper v-bind:style='{height:heights[3]}'class='date-paper' ></mu-paper>
				</div>
				<div class='date-text'>{{ dates[3] }}</div>
			</div>
			<div id='date5' class='date'>
				<div class='date-paper-container'>
					<div v-bind:style='{top:step_num_tops[4]}' class='steps-num'>{{ steps[4] }}</div>
					<mu-paper v-bind:style='{height:heights[4]}' class='date-paper'></mu-paper>
				</div>
				<div class='date-text'>{{ dates[4] }}</div>
			</div>
		</div>
	</div>
</template>

<script>
export default {
	props: ['url'],
	data() {
		return {
			dates: ['2017-06-23','2017-06-24','2017-06-25','2017-06-26','2017-06-27'],
			steps: [823, 101, 311, 923, 24],
			heights: [0, 0, 0, 0, 0],
			step_num_tops: ['403px', '403px', '403px', '403px', '403px']
		}
	},
	methods: {

	},
	beforeMount() {
		let max = this.steps[0]
		this.steps.forEach (function(item){
			if(item > max) {
				max = item
			}
		})
		if (max> 0) {
			for (let i = 0; i < this.steps.length; ++i) {
				this.heights[i] = this.steps[i]/max*0.8*0.9*560 + 'px'
				this.step_num_tops[i] = 560*0.8*0.9 - this.steps[i]/max*0.8*0.9*560 + 'px'
			}
		}
	}
}
</script>


<style>

#main-page {
	width: 100%;
	height: 100%;
	background-image: url(../assets/images/run2.jpg);
	background-color: #5A4F60;

	background-size: 100%;
	background-repeat: round;
}

#main-page-container {
	position: relative;
	margin: auto;
	top: 10%;
	width: 800px;
	height: 560px;
	opacity: 0.8;
}

.date {
	position: relative;
	width: 20%;
	height: 100%;
	float: left;
}

.date-paper-container {
	position: relative;
	width: 100%;
	height: 80%;
}

.steps-num {
	position: relative;
	height: 10%;
	text-align: center;
	font-weight: bold;
	color: #FFFFFF;
	font-weight: 
}

.date-paper {
	position: absolute;
	height: 90%;
	bottom: 0%;
	width: 100%;
	background-color: #9A9F60;
}

.date-text {
	position: relative;
	top: 5%;
	text-align: center;
	font-weight: bold;
	color: #FFFFFF;
	font-weight: 
}


</style>