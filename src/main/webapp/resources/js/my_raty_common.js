$(document).ready(function(){
    var ratings_scores = [
        "<span class=\"rating-scale\">Hate it / Don't want it at all<\/span>",
        "<span class=\"rating-scale\">Not bad<\/span>",
        "<span class=\"rating-scale\">Ok<\/span>",
        "<span class=\"rating-scale\">Like it<\/span>",
        "<span class=\"rating-scale\">Love it / I want it so much!<\/span>",
        "<span class=\"rating-scale\">No opinion<\/span>"
    ];

    var i=0;
    $(".rating-example").each(function(){
        $(this).append(ratings_scores[i]);
        i++;
    });

    var j=0;
    $(".rating-example-modal").each(function(){
        $(this).append(ratings_scores[j]);
        j++;
    });
});