package eus.martico.fileTransfer.seguridad.date.service;

import java.util.Date;

//import org.joda.time.DateTime;


/**
 * Provides the current date.
 * 
 * @author 
 */
public interface DateService {

  /**
   * @return current date at the moment of the call
   */
  Date now();
  //LocalDateTime now();
}
