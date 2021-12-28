package com.maglab.cal;

import java.util.concurrent.TimeUnit;

public class CalUpdater  extends Thread {
	private boolean running = true;
	CalParser cp = new CalParser();
	
	@Override
	public void run() {
		// Keeps running indefinitely, until the termination flag is set to false
		while (running) {
			//selectMessage();
			
			cp.load_calendar();
			try {
				TimeUnit.MINUTES.sleep(60*8);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//update_dups();
			//selectFixMessage();
		}
	}
	// Terminates thread execution
		public void halt() {
			this.running = false;
		}
 
}
