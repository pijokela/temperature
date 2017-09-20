package service

import javax.inject.Inject
import i2creader.I2cService
import controllers.Measurement
import w1reader.W1Service
import org.joda.time.DateTime
import scala.concurrent.Future

import scala.concurrent.ExecutionContext.Implicits.global

class MeasurementService @Inject()(w1t: W1Service, i2ct: I2cService) {
  
  def measure(): Future[List[Measurement]] = {
    val now = new DateTime()
    val w1f = w1t.measure(now)
    val i2cf = i2ct.measure(now)
    
    for(
      w1 <- w1f;
      i2c <- i2cf
    ) yield w1 ++ i2c
  }
  
}