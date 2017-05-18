$(function() {
  console.log("start");
  $(".button").click(function() {
    console.log('click');
    $.ajax({
      type : "POST",
      url : "/api/user/login",
      data: $('#loginForm').serialize(),
      success : function (data) {
        if (data == 'user login success!')
          $(location).attr('href', '/');
        else
          $('#loginForm').before('<h1>user login fail</h1>');
      },
      error: function(data) {
           	alert("error:" + data);
      }
    });
  });
});