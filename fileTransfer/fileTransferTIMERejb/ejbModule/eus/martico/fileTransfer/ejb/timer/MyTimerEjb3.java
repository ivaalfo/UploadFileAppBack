//package eus.martico.fileTransfer.ejb.timer;
//
//import java.util.Date;
//import java.util.logging.Logger;
//
//import javax.annotation.Resource;
//import javax.ejb.Stateless;
//import javax.ejb.Timeout;
//import javax.ejb.Timer;
//import javax.ejb.TimerService;
//
//@Stateless
//public class MyTimerEjb3 implements MyITimer {
//	/**
//	 * Default constructor.
//	 */
//	public MyTimerEjb3() {
//// constructor stub
//	}
//
//	private static final Logger log = Logger.getLogger(MyTimerEjb3.class.getName());
//
//	private static final String TM_NAME = "MyTestTimer";
//
//	@Resource
//	TimerService timerService;
//
//	@Timeout
//	public void myTimeout(Timer timer) {
//// Why is it not called when MyTestTimer expires?
//		System.out.println("Timeout myTimeout() called");
//		System.out.println("... called at: " + new Date(System.currentTimeMillis()));
//
//		log.info("Timer fired: timer=" + timer.getInfo());
//	}
//	@Override
//	public void startTimer() {
//		log.info("Start timer");
//		log.info("...but first Stop any timers");
//		stopTimer();
//// Create time expiring in 10 sec
//		long duration = 1000 * 10;
//
//		Timer timer = timerService.createTimer(duration, TM_NAME);
//		System.out.println("Created Timer at : " + new Date(System.currentTimeMillis()));
//		log.info("Timer created: timer=" + timer.getInfo());
//		log.info("TimeRemaining=" + timer.getTimeRemaining());
//		log.info("Next timeout=" + timer.getNextTimeout());
//
//	}
//	@Override
//	public void stopTimer() {
//		log.info("Stop timer");
//
//		for (Object tm : timerService.getTimers()) {
//			Timer timer = (Timer) tm;
//
//			log.info("Cancel timer: timer=" + timer.getInfo());
//			timer.cancel();
//		}
//	}
//	@Override
//	public void printTimer() {
//		log.info("Print timer");
//
//		for (Object tm : timerService.getTimers()) {
//			Timer timer = (Timer) tm;
//
//			log.info("timer=" + timer.getInfo());
//		}
//	}
//}