package io.magentys.donut.gherkin

import java.io.File

import io.magentys.donut.gherkin.model._
import io.magentys.donut.gherkin.processors.JSONProcessor
import io.magentys.donut.log.Log
import io.magentys.donut.performance.PerformanceSupport
import io.magentys.donut.template.TemplateEngine
import io.magentys.donut.transformers.cucumber.CucumberTransformer
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat

import scala.util.Try

object Generator extends Log with PerformanceSupport {

  val formatter = DateTimeFormat.forPattern("yyyy-MM-dd-HHmm")

  //this wrapper is currently used to help the java maven plugin
  def apply(sourcePath: String,
            outputPath: String = "donut",
            filePrefix: String = "",
            dateTime: String,
            template: String = "default",
            countSkippedAsFailure: Boolean = false,
            countPendingAsFailure: Boolean = false,
            countUndefinedAsFailure: Boolean = false,
            countMissingAsFailure: Boolean = false,
            projectName: String,
            projectVersion: String,
            customAttributes: scala.collection.mutable.Map[String, String]): ReportConsole = {

    createReport(sourcePath, outputPath, filePrefix, dateTime, template, countSkippedAsFailure, countPendingAsFailure,
      countUndefinedAsFailure, countMissingAsFailure, projectName, projectVersion, customAttributes.toMap) match {
      case Right(report) => ReportConsole(report)
      case Left(error) => throw new DonutException(s"An error occurred while generating donut report. $error")
    }
  }

  private[gherkin] def createReport(sourcePath: String,
                                    outputPath: String = "donut",
                                    filePrefix: String = "",
                                    datetime: String = formatter.print(DateTime.now),
                                    template: String = "default",
                                    countSkippedAsFailure: Boolean = false,
                                    countPendingAsFailure: Boolean = false,
                                    countUndefinedAsFailure: Boolean = false,
                                    countMissingAsFailure: Boolean = false,
                                    projectName: String,
                                    projectVersion: String,
                                    customAttributes: Map[String, String] = Map()): Either[String, Report] = {

    //Prepare objects
    val statusConf = StatusConfiguration(countSkippedAsFailure, countPendingAsFailure, countUndefinedAsFailure, countMissingAsFailure)
    val projectMetadata = ProjectMetadata(projectName, projectVersion, customAttributes)
    val sourceDir = new File(sourcePath)
    val reportStartedTimestamp = Try(formatter.parseDateTime(datetime)).getOrElse(DateTime.now)

    for {
      folder          <- if (sourceDir.exists()) Right(sourceDir).right else Left("Source directory doesn't exist").right
      jsons           <- JSONProcessor.loadFrom(folder).right
      _               <- if (jsons.isEmpty) Left("No files found of correct format").right else Right(jsons).right
      features        <- timed("step1", "Loaded JSON files") { CucumberTransformer.transform(jsons, statusConf).right }
      report          <- timed("step3", "Produced report") { Right(Report(features, reportStartedTimestamp, projectMetadata)).right }
      _               <- TemplateEngine(report, s"/templates/$template/index.html").renderToHTML(outputPath, filePrefix).right
    } yield report
  }
}

case class DonutException(mgs: String) extends Exception
