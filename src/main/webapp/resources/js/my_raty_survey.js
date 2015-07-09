function rateProductAjax(prod_id, score, category) {
    console.log("category: " + category);
    console.log("category.toString: " + category.toString());

    $.ajax({
        type: 'POST',
        url : '<c:url value="/survey/rate" />',
        data: "prod_id=" + prod_id + "&score=" + score + "&category=" + category.toString()
    });
}

$(document).ready(function(){
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
            rateProductAjax(this.id, score, $(".product-category").attr('data-category'));
        }
    });
});