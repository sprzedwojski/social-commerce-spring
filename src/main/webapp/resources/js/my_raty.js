$('.rating').raty({
    cancel  : true,
    path: "resources/raty/images/",
    starOff : 'star-off-big.png',
    starOn  : 'star-on-big.png',
    cancelOff: 'cancel-off-big.png',
    cancelOn: 'cancel-on-big.png',
    hints: ['hate it', 'not bad', 'ok', 'like it', 'love it'],
    score: function() {
        return $(this).attr('data-score');
    },
    click: function(score, evt) {
        rateProductAjax(this.id, score);
    }
});

// TODO co jeśli sesja wygaśnie??
function rateProductAjax(prod_id, score) {
    $.ajax({
        type: 'POST',
        url : '<c:url value="/survey" />',
        data: "prod_id=" + prod_id + "&score=" + score
    });
}

$(document).ready(function(){
    $("[rel=tooltip]").tooltip({ placement: 'bottom'});
});