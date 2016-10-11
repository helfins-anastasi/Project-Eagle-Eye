package com.helfinstein.zachary.eagleeye

import com.helfinstein.zachary.eagleeye.State.MovieState
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

case class Movie(id: String, name: String, width: Int, imageUrl: String, description: String)
object MovieDatabase {
  val csl = Movie("a145092k", "Crazy, Stupid, Love.", 250, "http://images5.fanpop.com/image/photos/24600000/Movie-Poster-crazy-stupid-love-24693875-1600-2333.jpg", "People are crazy because they're in love")
  val f13 = Movie("sl3t5l54", "Friday the 13th", 250, "http://www.originalprop.com/blog/wp-content/uploads/2009/03/friday-the-13th-poster-onesheet-high-resolutionx2600.jpg", "Classic scary movie")

  def getMovie(id: String): Option[Movie] = id match {
    case id if id == "a145092k" => Some(csl)
    case id if id == "sl3t5l54" => Some(f13)
    case _ => None
  }
}

case class MoviePair(parent: Option[MoviePair], leftId: String, rightId: String, state: MovieState = State.pending)
object MoviePair {
  def apply() : MoviePair = new MoviePair(None, "a145092k", "sl3t5l54")
}

object State extends Enumeration {
  type MovieState = Value
  val leftChosen, leftPreferred, rightPreferred, rightChosen, pending = Value
}

class EagleEyeController extends Controller {

  // Instrument this to alert when value approaches Long.MAX_VALUE / 2
  // Also, look into other methods for generating ID's
  var availableId : Long = 0
  //TODO: Safely & efficiently reuse IDs when associated data is removed

  //var movieTrees : Map[Int, MovieTree] = Map.empty

  get("/") { request: Request =>
    info("index.html")
    response.ok.file("/index.html")
  }

  get("/script.js") { request: Request =>
    response.ok.file("/script.js")
  }

  get("/style.css") { request: Request =>
    response.ok.file("/style.css")
  }

  get("/first") { request: Request =>
    val pair = MoviePair()
    response.ok.json(pair)
  }

  get("/next") { request: Request =>
    response.notFound("Sorry, there are no more movies to look at :( Come back soon!! We're still in alpha testing.")
  }

  get("/movie") { request: Request =>
    val id = request.getParam("id")
    MovieDatabase.getMovie(id) match {
      case Some(movie) => response.ok.json(movie)
      case None => response.notFound("Movie with id \"" + id + "\" not found.")
    }
  }
}