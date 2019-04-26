var date = document.getElementById("date").value
$(function(){
    $('#extend').popover({

        placement: 'right',
        title: 'Deadline',
        trigger: 'click',
        html:true,
        content: $('#myForm').html()
    }).on('click', function(){
// had to put it within the on click action so it grabs the correct info on submit
        $('.btn-dark').click(function(){
            //var date = $('#date').val();
            // var now = new Date().getTime();
            // if (now - date.getTime() < 0) {
            // }
            //var deadline = "Deadline: "+$('#date').val().getTime();
            $('#deadline').text(Date.parse($('#date')).toString());
            $('#popForm').ajax({
                type:'POST',//тип запроса
                data:{extendDeadline: ''},//параметры запроса
                url:'<c:url value="viewpost/1"/> ',//url адрес обработчика
                success: result//возвращаемый результат от сервера
            });

            //$('#myForm').text(result);
            //$('#extend').popover('hide')
            // $.post('/extendDeadline/3', {
            //     date: $('#date').val(),
            // })

            // $('#result').after("form submitted by " + $('#date').val())
            // $.post('/extendDeadline', {
            //     date: $('#date').val(),
            // }, function(r){
            //
            //     $('#result').html('response from server could be here' )
            // })
        })
    })
})

// $(document).ready(function () {
//
//     $("#search-form").submit(function (event) {
//
//         //stop submit the form, we will post it manually.
//         event.preventDefault();
//
//         fire_ajax_submit();
//
//     });
//
// });
//
// function fire_ajax_submit() {
//
//     var search = {}
//     search["username"] = $("#username").val();
//
//     $("#btn-search").prop("disabled", true);
//
//     $.ajax({
//         type: "POST",
//         contentType: "application/json",
//         url: "/api/search",
//         data: JSON.stringify(search),
//         dataType: 'json',
//         cache: false,
//         timeout: 600000,
//         success: function (data) {
//
//             var json = "<h4>Ajax Response</h4><pre>"
//                 + JSON.stringify(data, null, 4) + "</pre>";
//             $('#feedback').html(json);
//
//             console.log("SUCCESS : ", data);
//             $("#btn-search").prop("disabled", false);
//
//         },
//         error: function (e) {
//
//             var json = "<h4>Ajax Response</h4><pre>"
//                 + e.responseText + "</pre>";
//             $('#feedback').html(json);
//
//             console.log("ERROR : ", e);
//             $("#btn-search").prop("disabled", false);
//
//         }
//     });
//
// }