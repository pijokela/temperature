package service

import javax.inject.Inject
import i2creader.I2cService
import controllers.Measurement
import w1reader.W1Service
import org.joda.time.DateTime
import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

import play.api.libs.ws.WSClient
import play.api.Configuration
import play.api.libs.json.JsArray
import play.api.libs.ws.WSAuthScheme

class MeasurementService @Inject()(w1t: W1Service, i2ct: I2cService, ws: WSClient, configuration: Configuration) {
  
  val url = configuration.get[String]("measurement.send-to")
  val username = configuration.get[String]("measurement_server.username")
  val password = configuration.get[String]("measurement_server.password")
  
  var recentMeasurements: List[Measurement] = Nil
  
  def sendMeasurements(measurements: List[Measurement]): Future[Unit] = {
    ws.url(url)
      .withAuth(username, password, WSAuthScheme.BASIC)
      .post(JsArray(measurements.map(_.toJson)))
      .map(_ => Unit)
  }
  
  def measure(): Future[List[Measurement]] = {
    val now = new DateTime()
    val w1f = if (w1t.isOnline())
      w1t.measure(now) else Future.successful(Nil)
    val i2cf = if (i2ct.isOnline())
      i2ct.measure(now) else Future.successful(Nil)
    
    val newMeasurementsFuture = for(
      w1 <- w1f;
      i2c <- i2cf
    ) yield {
      val ml = w1 ++ i2c
      recentMeasurements = (ml ++ recentMeasurements).take(100)
      ml
    }
    
    newMeasurementsFuture
      .flatMap(measurements => sendMeasurements(measurements))
      .flatMap(_ => newMeasurementsFuture)
  }
  
}