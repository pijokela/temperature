package controllers

import javax.inject._
import play.api._
import play.api.mvc._
import service.MeasurementService
import play.api.libs.json.JsArray

import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class HomeController @Inject()(cc: ControllerComponents, measurementService: MeasurementService) extends AbstractController(cc) {

  def index() = Action { implicit request: Request[AnyContent] =>
    Ok(JsArray(
        measurementService.recentMeasurements.map(_.toJson)
    ))
  }
  
  def measure() = Action.async { implicit request: Request[AnyContent] =>
    measurementService.measure().map { measurements: List[Measurement] =>
      Ok(JsArray(measurements.map(_.toJson)))
    }
  }
}
