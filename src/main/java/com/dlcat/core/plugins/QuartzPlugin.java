package com.dlcat.core.plugins;

import java.util.List;

import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.dlcat.core.model.ToTask;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.IPlugin;


public class QuartzPlugin implements IPlugin {

	private SchedulerFactory sf = null;

	public static Scheduler scheduler = null;

	/**
	 * 启动Quartz
	 */
	public boolean start() {
		// 创建调度工厂
		sf = new StdSchedulerFactory();

		try {
			scheduler = sf.getScheduler();

			List<ToTask> tasks = ToTask.dao.findAll();
			for (com.dlcat.core.model.ToTask task : tasks) {
				String jobClassName = task.getStr("clazz");
				String jobCronExp = task.getStr("exp");
				int state = task.getInt("state");

				Class clazz;
				try {
					clazz = Class.forName(jobClassName);
				} catch (ClassNotFoundException e) {
					throw new RuntimeException(e);
				}

				JobDetail job = JobBuilder.newJob(clazz).withIdentity(jobClassName, jobClassName).build();
				CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobClassName, jobClassName).withSchedule(CronScheduleBuilder.cronSchedule(jobCronExp)).build();

				try {
					scheduler.scheduleJob(job, trigger);
					if (state == ToTask.STATE_STOP) {
						// 暂停触发
						scheduler.pauseTrigger(trigger.getKey());
					}
				} catch (SchedulerException e) {
					new RuntimeException(e);
				}

				LogKit.info(job.getKey() + " loading and exp: " + trigger.getCronExpression());
			}

			scheduler.start();

		} catch (SchedulerException e) {
			new RuntimeException(e);
		}

		return true;

	}

	/**
	 * 停止Quartz
	 */
	public boolean stop() {
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			LogKit.error("shutdown error", e);
			return false;
		}
		return true;
	}
}