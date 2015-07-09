$(document).ready(function(){
    $('.rating-example').raty({
        cancel  : true,
        path: "resources/raty/images/",
        starOff : 'star-off-big.png',
        starOn  : 'star-on-big.png',
        cancelOff: 'cancel-off-big.png',
        cancelOn: 'cancel-on-big.png',
        readOnly: true,
        hints: ['hate it', 'not bad', 'ok', 'like it', 'love it'],
        score: function() {
            return $(this).attr('data-score');
        }
    });

    $('.rating-example-modal').raty({
        cancel  : true,
        path: "resources/raty/images/",
        starOff : 'star-off-big.png',
        starOn  : 'star-on-big.png',
        cancelOff: 'cancel-off-big.png',
        cancelOn: 'cancel-on-big.png',
        readOnly: true,
        hints: ['hate it', 'not bad', 'ok', 'like it', 'love it'],
        score: function() {
            return $(this).attr('data-score');
        }
    });

});