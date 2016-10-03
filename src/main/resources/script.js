function getHtml(movie) {
    var html = "<h3>" + movie.name + "</h3>"
    if(movie.image_url)
        html += '<img src="' + movie.image_url + '" width="'+movie.width+'px" />'
    if(movie.description)
        html += '<p>' + movie.description + '</p>'
    return html
}

function getTwoMovies() {
    $.ajax("/movie", {success: setMovie("left")})
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