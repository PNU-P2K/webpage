/*!
* Start Bootstrap - Simple Sidebar v6.0.6 (https://startbootstrap.com/template/simple-sidebar)
* Copyright 2013-2023 Start Bootstrap
* Licensed under MIT (https://github.com/StartBootstrap/startbootstrap-simple-sidebar/blob/master/LICENSE)
*/
// 
// Scripts
//

function subminJoin() {
    alert("회원가입");
    let data = {
        id: $("#id").val(),
        password: $("#password").val(),
        username: $("#name").val(),
        email: $("#email").val(),
        role: $("#role").val()
    }

    $.ajax({
        // 회원가입 수행 요청
        type: "POST",
        url: "/join",
        data: JSON.stringify(data), // http body 데이터
        contentType: "application/json; charset=utf-8", // body 데이터가 어떤 타입인지 (MIME)
        dataType: "json" // 요청을 서버로 해서 응답이 왔을 때 기본적으로 모든 것이 String(문자열), 만약 생긴게 json이라면 javascript 오브젝트로 변경
    }).done(function (resp) {
        alert("회원가입이 완료되었습니다.");
        location.href = "/login";
    }).fail(function (error) {
        alert("회원가입이 실패하였습니다.");
        alert(JSON.stringify(error));
    });
}

let index = {
    init: function () {
        $("#join-btn").on("submit", ()=>{ // function(){} 대신 ()=>{} 를 쓴 이유 : this를 바인딩하기 위해서
            this.save();
        });
    },

    save: function () {
        alert("회원가입");
        let data = {
            id: $("#id").val(),
            password: $("#password").val(),
            username: $("#name").val(),
            email: $("#email").val(),
            role: $("#role").val()
        }

        $.ajax({
            // 회원가입 수행 요청
            type: "POST",
            url: "/join",
            data: JSON.stringify(data), // http body 데이터
            contentType: "application/json; charset=utf-8", // body 데이터가 어떤 타입인지 (MIME)
            dataType: "json" // 요청을 서버로 해서 응답이 왔을 때 기본적으로 모든 것이 String(문자열), 만약 생긴게 json이라면 javascript 오브젝트로 변경
        }).done(function (resp) {
            alert("회원가입이 완료되었습니다.");
            location.href = "/login";
        }).fail(function (error) {
            alert("회원가입이 실패하였습니다.");
            alert(JSON.stringify(error));
        });
    }
}

index.init();

window.addEventListener('DOMContentLoaded', event => {

    // Toggle the side navigation
    const sidebarToggle = document.body.querySelector('#sidebarToggle');
    if (sidebarToggle) {
        // Uncomment Below to persist sidebar toggle between refreshes
        // if (localStorage.getItem('sb|sidebar-toggle') === 'true') {
        //     document.body.classList.toggle('sb-sidenav-toggled');
        // }
        sidebarToggle.addEventListener('click', event => {
            event.preventDefault();
            document.body.classList.toggle('sb-sidenav-toggled');
            localStorage.setItem('sb|sidebar-toggle', document.body.classList.contains('sb-sidenav-toggled'));
        });
    }
});