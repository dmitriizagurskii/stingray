var date = document.getElementById("date").value
$(function(){
    $('#extend').popover({

        placement: 'right',
        title: 'Deadline',
        trigger: 'click',
        html:true,
        content: $('#myForm').html()
    })
})