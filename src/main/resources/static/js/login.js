var txtError1 = document.getElementById('error_box1');
var txtError2 = document.getElementById('error_box2');

$('#loginBtn').click(function() {
    var userName = $('#username').val();
    var password = $('#password').val();

    console.log("login",userName, password);
    $.post('http://47.102.152.12:8080/user/login', {
        'userName': userName,
        'password': password
    }, function(res) {
        console.log(res);
        if(res === "true") {
            document.cookie = "userName=" + userName;
            window.location.href = "mysports"
        } else {
            $(txtError1).text("登录失败");
            $(txtError1).fadeIn(1000);
            $(txtError1).fadeOut(1000)
        }
    })
});
$('#signupBtn').click(function() {
    var userName = $('#usernamesignup').val();
    var password = $('#passwordsignup').val();
    var repeat = $('#passwordsignup_confirm').val();

    console.log("reg",userName, password);
    if(repeat !== password) {
        $(txtError2).text("两次密码不同");
        $(txtError2).fadeIn(1000);
        $(txtError2).fadeOut(1000);
        return;
    }
    $.post('http://47.102.152.12:8080/user/reg', {
        'userName': userName,
        'password': password
    }, function(res) {
        console.log(res);
        if(res === "true") {
            $(txtError2).text("注册成功");
            $(txtError2).fadeIn(1000);
            location.reload();
        } else {
            $(txtError2).text("用户名已存在");
            $(txtError2).fadeIn(1000);
            $(txtError2).fadeOut(1000)
        }
    })

});