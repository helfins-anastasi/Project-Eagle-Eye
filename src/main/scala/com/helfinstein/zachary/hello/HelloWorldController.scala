package com.helfinstein.zachary.hello

import com.helfinstein.zachary.hello.State.MovieState
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

case class Movie(name: String, width: Int, imageUrl: String, description: String)
case class MovieTree(movie: Movie, next: MovieTree, sibling: MovieTree, state: MovieState = State.pending)

object State extends Enumeration {
  type MovieState = Value
  val chosen, preferred, notPreferred, notChosen, pending = Value
}

class HelloWorldController extends Controller {

  // Instrument this to alert when value approaches Long.MAX_VALUE / 2
  // Also, look into other methods for generating ID's
  var availableId : Long = 0;
  //TODO: Safely & efficiently reuse IDs when associated data is removed

  var movieTrees : Map[Int, MovieTree] = Map.empty

  get("/") { request: Request =>
    info("index.html")
    response.ok.file("/index.html")
  }

  get("/newId") { request: Request =>
    info("new ID: " + availableId)
    val id = availableId
    availableId += 1
    id
  }

  get("/script.js") { request: Request =>
    response.ok.file("/script.js")
  }

  get("/style.css") { request: Request =>
    response.ok.file("/style.css")
  }

  var nextMovie = Movie("Crazy, Stupid, Love.", 250, "http://images5.fanpop.com/image/photos/24600000/Movie-Poster-crazy-stupid-love-24693875-1600-2333.jpg", "People are crazy because they're in love")
  var lastMovie = Movie("Friday the 13th", 250, "http://www.originalprop.com/blog/wp-content/uploads/2009/03/friday-the-13th-poster-onesheet-high-resolutionx2600.jpg", "Classic scary movie")

  get("/movie") { request: Request =>
    val temp = lastMovie
    lastMovie = nextMovie
    nextMovie = temp
    response.ok.json(lastMovie)
  }
}