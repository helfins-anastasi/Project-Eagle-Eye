package com.helfinstein.zachary.eagleeye

import com.helfinstein.zachary.eagleeye.State.MovieState
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

// TODO: look into Recommendation engines
// TODO: look into Netflix Max

case class Movie(id: String, name: String, width: Int, imageUrl: String, description: String)
object MovieDatabase {
  val csl = Movie("tt1570728", "Crazy, Stupid, Love.", 250, "http://ia.media-imdb.com/images/M/MV5BMTg2MjkwMTM0NF5BMl5BanBnXkFtZTcwMzc4NDg2NQ@@._V1_SX300.jpg", "A middle-aged husband's life changes dramatically when his wife asks him for a divorce. He seeks to rediscover his manhood with the help of a newfound friend, Jacob, learning to pick up girls at bars.")
  val f13 = Movie("tt0080761", "Friday the 13th", 250, "https://images-na.ssl-images-amazon.com/images/M/MV5BNWMxYTYzYWQtNGZmNy00MTg5LTk1N2MtNzQ5NjQxYjQ5NTJhXkEyXkFqcGdeQXVyMTQxNzMzNDI@._V1_SX300.jpg", "A group of camp counselors is stalked and murdered by an unknown assailant while trying to reopen a summer camp which, years before, was the site of a child's drowning.")
  val fnf = Movie("tt1013752", "Fast & Furious", 250, "https://images-na.ssl-images-amazon.com/images/M/MV5BMTQwNDA2MTg3Nl5BMl5BanBnXkFtZTcwNTg3MzIyMg@@._V1_SX300.jpg", "Brian O'Conner, now working for the FBI in LA, teams up with Dominic Toretto to bring down a heroin importer by infiltrating his operation.")
  val ring = Movie("tt0298130", "The Ring", 250, "https://images-na.ssl-images-amazon.com/images/M/MV5BNDA2NTg2NjE4Ml5BMl5BanBnXkFtZTYwMjYxMDg5._V1_SX300.jpg", "A journalist must investigate a mysterious videotape which seems to cause the death of anyone in a week of viewing it.")
  val essm = Movie("tt0338013", "Eternal Sunshine of the Spotless Mind", 250, "https://images-na.ssl-images-amazon.com/images/M/MV5BMTY4NzcwODg3Nl5BMl5BanBnXkFtZTcwNTEwOTMyMw@@._V1_SX300.jpg", "When their relationship turns sour, a couple undergoes a procedure to have each other erased from their memories. But it is only through the process of loss that they discover what they had to begin with.")
  val lorax = Movie("tt1482459", "The Lorax", 250, "https://images-na.ssl-images-amazon.com/images/M/MV5BMTU1MTAwMjk1NF5BMl5BanBnXkFtZTcwMDI5NDc4Ng@@._V1_SX300.jpg", "A 12-year-old boy searches for the one thing that will enable him to win the affection of the girl of his dreams. To find it he must discover the story of the Lorax, the grumpy yet charming creature who fights to protect his world.")

  def getMovie(id: String): Option[Movie] = id match {
    case "tt1570728" => Some(csl)
    case "tt0080761" => Some(f13)
    case "tt1013752" => Some(fnf)
    case "tt0298130" => Some(ring)
    case "tt0338013" => Some(essm)
    case "tt1482459" => Some(lorax)
    case _ => None
  }
}

case class MoviePair(parent: Option[MoviePair], leftId: String, rightId: String, var state: MovieState = State.pending) {
  def next(id: String) : Option[MoviePair] = id match {
    case "tt0080761" => {
      state = State.rightPreferred
      Some(new MoviePair(Some(this), "tt1013752", "tt0298130"))
    }
    case "tt1570728" => {
      state = State.leftPreferred
      Some(new MoviePair(Some(this), "tt0338013", "tt1482459"))
    }
    case _ => None
  }
}
object MoviePair {
  def apply() : MoviePair = new MoviePair(None, "tt1570728", "tt0080761")
  var current : MoviePair = MoviePair()
}

object State extends Enumeration {
  type MovieState = Value
  val leftChosen, leftPreferred, rightPreferred, rightChosen, pending = Value
}

class EagleEyeController extends Controller {

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
    MoviePair.current = pair
    response.ok.json(pair)
  }

  get("/next") { request: Request =>
    val id = request.getParam("chosen_id")
    info("Chosen ID: " + id)
    MoviePair.current.next(id) match {
      case Some(pair) => {
        MoviePair.current = pair
        response.ok.json(pair)
      }
      case None => response.notFound ("Sorry, we don't have any more suggestions for this movie :(\nCome back soon!! We're still in alpha testing.")
    }
  }

  get("/watch") { request: Request =>
    val id = request.getParam("id")
    id match {
      case i if i == MoviePair.current.leftId => MoviePair.current.state = State.leftChosen
      case i if i == MoviePair.current.rightId => MoviePair.current.state = State.rightChosen
    }
    MovieDatabase.getMovie(id) match {
      case Some(movie) => response.ok.json(movie)
      case None => response.notFound("Movie with id \"" + id + "\" not found.")
    }
  }

  get("/movie") { request: Request =>
    val id = request.getParam("id")
    MovieDatabase.getMovie(id) match {
      case Some(movie) => response.ok.json(movie)
      case None => response.notFound("Movie with id \"" + id + "\" not found.")
    }
  }
}