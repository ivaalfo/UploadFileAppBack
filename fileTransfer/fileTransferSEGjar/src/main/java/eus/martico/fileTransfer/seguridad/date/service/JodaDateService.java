package eus.martico.fileTransfer.seguridad.date.service;


import java.util.Calendar;
import java.util.Date;

import org.springframework.stereotype.Service;

//import static com.google.common.base.Preconditions.checkNotNull;
@Service
final class JodaDateService implements DateService {

@SuppressWarnings("unused")
private final Date timeZone;

  /**
   * Force system-wide timezone to ensure consistent
   * dates over all servers, independently from the region
   * the server is running.
   */
  JodaDateService(final Date timeZone) {
    super();
    this.timeZone = checkNotNull(timeZone);

    //System.setProperty("user.timezone", timeZone.getID());
    //TimeZone.setDefault(timeZone.toTimeZone());
    //DateTimeZone.setDefault(timeZone);
  }

//  @Override
//  public DateTime now() {
//    return DateTime.now(timeZone);
//  }
  @Override
  public Date now() {
    //return DateTime.now(timeZone);
	return Calendar.getInstance().getTime();	  
    //return timeZone;
  }
  
  private static <T> T checkNotNull(T reference) {
    if (reference == null) {
      throw new NullPointerException();
    }
    return reference;
  }

}
