package org.mauritania.photosync.starter.gui

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import java.util.concurrent.Executors
import org.slf4j.LoggerFactory
import scalafx.application.Platform

object GuiAsync {

  private val logger = LoggerFactory.getLogger(this.getClass)

  val ThreadPoolSize = 4

  private val AsyncExecutionContext = new ExecutionContext {
    val ThreadPool = Executors.newFixedThreadPool(ThreadPoolSize);
    override def execute(runnable: Runnable) = ThreadPool.submit(runnable)
    override def reportFailure(t: Throwable): Unit = logger.error("Error", t)
  }

  /**
    * Execute sync(async()).
    *
    * Motivation: update of UI elements has to be done using UI thread, which
    * if used for querying slow APIs, would impact user experience.
    *
    * @param async function returning [[T]] (will asynchronously execute)
    * @param sync  function using [[T]] (will execute synchronously in UI thread)
    * @tparam T type of exchange
    */
  def asyncThenSync[T](async: => T, sync: T => Unit) = {
    val fu = Future(async)(AsyncExecutionContext)
    fu.onComplete {
      case Success(r) => Platform.runLater(runnable(sync(r)))
      case Failure(e) => logger.error(e.getMessage)
    }(AsyncExecutionContext)
  }

  private def runnable(func: => Unit): Runnable = new Runnable {
    override def run = func
  }

}

