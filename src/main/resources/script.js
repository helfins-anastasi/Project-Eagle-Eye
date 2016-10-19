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
        html += '<img src="' + movie.image_url + '" width="'+movie.width+'px" onclick="chooseMovie(\''+movie.id+'\')"/>'
    if(movie.description)
        html += '<p>' + movie.description + '</p>'
    html += '<button onclick="watchMovie(\''+movie.id+'\')">Watch</button>'
    return html
}

function chooseMovie(id) {
    console.log("chooseMovie("+id+") called")
    $.ajax("/next", {
        data:{"chosen_id": id},
        error:function(obj,status,error){alert(obj.responseText)},
        success:function(pair) {
            document.moviePair = pair
            getMovies()
        }
    })
}

function watchMovie(id) {
    console.log("watchMovie called")
    $.get("/watch", {"id": id}, function(movie) {
        alert("Wonderful! You've chosen " + movie.name)
    })
}

function getMovies() {
    console.log("movie pair: ")
    console.log(document.moviePair)
    $.get("/movie", {"id": document.moviePair.left_id}, setLeft)
    $.get("/movie", {"id": document.moviePair.right_id}, setRight)
    $("#findMovie").hide()
}

function setMovie(movie, cssId, side) {
    wide = movie.width + 100
    $(cssId)
        .html(getHtml(movie))
        .width(wide + "px")
        .css("position", "absolute")
        .css(side, "0")
        .css("top", "0")
        .children("img")
            .hover(startHover, endHover)
}

function startHover(event) {
    event.target.style.borderColor = "red"
}

function endHover(event) {
    event.target.style.borderColor = "white"
}

function setLeft(movie) {
    setMovie(movie, "#movie1", "left")
    $("#movie1").parent()
            .width((movie.width + 100) * 2 + "px")
            .css("left", ($("body").width() / 2 - movie.width - 100) + "px")
}

function setRight(movie) {
    setMovie(movie, $("#movie2"), "right")
}