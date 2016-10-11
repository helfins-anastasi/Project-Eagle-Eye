console.log("EXECUTING")
$.ajax("/first", {success: function(data) {
  console.log("CALLBACK")
  console.log(data)
  document.moviePair = data
  $("#findMovie").removeAttr("hidden")
  $("#loading").hide()
}})

function getHtml(movie) {
    var html = "<h3>" + movie.name + "</h3>"
    if(movie.image_url)
        html += '<img src="' + movie.image_url + '" width="'+movie.width+'px" />'
    if(movie.description)
        html += '<p>' + movie.description + '</p>'
    //html += '<button onclick="watchMovie('+movie.id+')">Watch '+movie.name+'</button>'
    //html += '<button onclick="chooseMovie('+''
    return html
}

function getMovies() {
    console.log("movie pair: ")
    console.log(document.moviePair)
    $.get("/movie", {"id": document.moviePair.left_id}, setLeft)
}

function setLeft(movie) {
    wide = movie.width + 100
    $("#movie1")
        .html(getHtml(movie))
        .width(wide + "px")
        .css("position", "absolute")
        .css("left", "0")
        .css("top", "0")
        .parent()
            .width(wide * 2 + "px")
            .css("left", ($("body").width() / 2 - wide) + "px")
    $.get("/movie", {"id": document.moviePair.right_id}, setRight)
}

function setRight(movie) {
    wide = movie.width + 100
    $("#movie2")
        .html(getHtml(movie))
        .width(wide + "px")
        .css("position", "absolute")
        .css("right", "0")
        .css("top", "0")
        .parent()
            .width(wide * 2 + "px")
            .css("left", ($("body").width() / 2 - wide) + "px")
}

function setMovie(side) {
    return function(movie) {
        wide = movie.width + 100
        $(side == "left" ? "#movie1" : "#movie2")
            .html(getHtml(movie))
            .width(wide + "px")
            .css("position", "absolute")
            .css(side, "0")
            .css("top", "0")
            .parent()
                .width(wide * 2 + "px")
                .css("left", ($("body").width() / 2 - wide) + "px")
        if(side == "left") {
            $.ajax("/movie", {success: setMovie("right")})
        }
  }
}