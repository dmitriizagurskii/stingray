Date.prototype.toDatetimeLocal = function toDatetimeLocal() {
    var
        date = this,
        ten = function (i) {
            return (i < 10 ? '0' : '') + i;
        },
        YYYY = date.getFullYear(),
        MM = ten(date.getMonth() + 1),
        DD = ten(date.getDate()),
        HH = ten(date.getHours()),
        II = ten(date.getMinutes());
    return YYYY + '-' + MM + '-' + DD + 'T' + HH + ':' + II;
};
var nextDay = new Date();
var maxDay = new Date();
nextDay.setDate(new Date().getDate()+1);
maxDay.setDate(new Date().getDate()+180);
var createTaskDate = document.getElementById('localdate');
createTaskDate.value = nextDay.toDatetimeLocal();
createTaskDate.max = maxDay.toDatetimeLocal();
createTaskDate.min = new Date().toDatetimeLocal();
var changeTaskDate = document.getElementById('changetaskdate');
changeTaskDate.max = maxDay.toDatetimeLocal();
changeTaskDate.min = new Date().toDatetimeLocal();