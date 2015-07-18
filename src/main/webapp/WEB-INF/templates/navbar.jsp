<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<nav class="navbar navbar-default navbar-fixed-top">
    <div class="container">
        <div class="navbar-header">
            <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
                <span class="sr-only">Toggle navigation</span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
                <span class="icon-bar"></span>
            </button>
            <a class="navbar-brand" href="#">Reco4Social</a>
        </div>
        <div id="navbar" class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
            </ul>
            <ul class="nav navbar-nav navbar-right">
                <li><a href="" data-toggle="modal" data-target="#aboutModal">About</a></li>
                <c:if test="${param.showHelp}">
                    <li><a href="" data-toggle="modal" data-target="#helpModal">Help</a></li>
                </c:if>
                <li><a href="mailto:szymon.przedwojski@gmail.com" target="_top">Report problem</a></li>
                <c:if test="${param.showLogout}">
                    <li><a href="<c:url value="/logout" />" >Logout</a></li>
                </c:if>
            </ul>
        </div>
    </div>
</nav>

<div id="aboutModal" class="modal fade" role="dialog">
    <div class="modal-dialog">

        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">About</h4>
            </div>
            <div class="modal-body">
                <p>Reco4Social is a recommender systems that works on user's social media information.<br>
                    To start generating recommendations, it first needs a group of initial ratings of products.</p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>

    </div>
</div>


<div id="helpModal" class="modal fade" role="dialog">
    <div class="modal-dialog">

        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Help</h4>
            </div>
            <div class="modal-body">
                <p><strong>Select categories of products that interest you
                    and rate items in those categories.</strong><br>
                    Imagine no financial limitations - this is about personal preferences only.</p>
                <p>The scale:<p>
                <p>
                <div class="rating-example-modal" data-score="1"></div><br>
                <div class="rating-example-modal" data-score="2"></div><br>
                <div class="rating-example-modal" data-score="3"></div><br>
                <div class="rating-example-modal" data-score="4"></div><br>
                <div class="rating-example-modal" data-score="5"></div><br>
                <div class="rating-example-modal" data-score="0"></div><br>
                </p>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>

    </div>
</div>