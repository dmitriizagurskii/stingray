$('#candidatesModal').on('show.bs.modal', function (event) {
    var button = $(event.relatedTarget)
    var candidateUsername = button.data('candidate-username')
    var postId = button.data('post-id')

    var modal = $(this)
    modal.find('.candidate').text(candidateUsername)
    modal.find('.choose').attr('action', '/candidates/' + postId + '/' + candidateUsername)
})