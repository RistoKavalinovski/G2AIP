package com.g2dev.integrator.schedule;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

public class Scheduler {

	private Runnable targetJob;

	private List<Integer> delayInMillis;

	final static Logger logger = Logger.getLogger(Scheduler.class);
	int nextDelayIndex = 0;

	public Scheduler(Runnable targetJob, int delaysInMillis) {

		this(targetJob, toList(delaysInMillis));
	}

	private static List<Integer> toList(int delaysInMillis) {
		List<Integer> l = new ArrayList<Integer>();
		l.add(delaysInMillis);
		return l;
	}

	public Scheduler(Runnable targetJob, List<Integer> delaysInMillis) {
		super();
		this.targetJob = targetJob;
		this.delayInMillis = delaysInMillis;
	}

	public void start() {
		while (true) {
			logger.info("Target Job '" + getTargetJob() + "' started at "
					+ new Date(System.currentTimeMillis()));
			System.out.println("Target Job '" + getTargetJob()
					+ "' started at " + new Date(System.currentTimeMillis()));
			getTargetJob().run();
			try {
				Thread.sleep(getNextDelay());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public int getNextDelay() {
		int curentDelay = getDelayInMillis().get(nextDelayIndex);
		if (getDelayInMillis().size() < (nextDelayIndex + 1)) {
			nextDelayIndex++;
		} else {
			nextDelayIndex = 0;
		}
		return curentDelay;
	}

	public void startThread() {
		new Thread(new Runnable() {

			public void run() {
				Scheduler.this.start();
			}
		}).start();
	}

	public Runnable getTargetJob() {
		return targetJob;
	}

	public void setTargetJob(Runnable targetJob) {
		this.targetJob = targetJob;
	}

	public List<Integer> getDelayInMillis() {
		return delayInMillis;
	}

	public void setDelayInMillis(List<Integer> delayInMillis) {
		this.delayInMillis = delayInMillis;
	}

}
