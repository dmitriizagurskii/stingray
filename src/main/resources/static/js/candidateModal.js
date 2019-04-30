$('#candidatesModal').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget)
    var candidateUsername = button.data('candidate-username')
    var taskId = button.data('task-id')

    var modal = $(this)
    modal.find('.candidate').text(candidateUsername)
    modal.find('.choose').attr('action', '/candidates/' + taskId + '/' + candidateUsername)
})