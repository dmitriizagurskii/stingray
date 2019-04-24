// Set the date we're counting down to
var postId = document.getElementsByClassName('postId');
var postNum = postId.length;
var countDownId = new Array(postNum);
var countDownDate = new Array(postNum);
var i;
for (i = 0; i < postNum; i++) {
    countDownId[i] = 'countdown' + postId[i].dataset.postid;
    countDownDate[i] = parseInt(document.getElementById(countDownId[i]).dataset.timeleft, 10);
}
// Update the count down every 1 second
var x = setInterval(function () {

    // Get todays date and time
    var now = new Date().getTime();

    // Find the distance between now and the count down date
    var distance = new Array(postNum);
    for (i = 0; i < postNum; i++) {
        distance[i] = countDownDate[i] - now;
        // Time calculations for days, hours, minutes and seconds
        var days = Math.floor(distance[i] / (1000 * 60 * 60 * 24));
        var hours = Math.floor((distance[i] % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60));
        var minutes = Math.floor((distance[i] % (1000 * 60 * 60)) / (1000 * 60));
        var seconds = Math.floor((distance[i] % (1000 * 60)) / 1000);
        document.getElementById(countDownId[i]).innerHTML = days + "d " + hours + "h "
            + minutes + "m " + seconds + "s ";
        // If the count down is finished, write some text
        if (distance[i] < 0)
            document.getElementById(postId[i]).innerHTML = "EXPIRED";
    }
}, 1000);