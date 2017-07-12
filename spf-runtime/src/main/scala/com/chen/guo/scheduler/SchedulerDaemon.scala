package com.chen.guo.scheduler

import com.chen.guo.FileIngestionJob

object SchedulerDaemon extends App {
  new FileIngestionJob().runJob(null, null)
}
