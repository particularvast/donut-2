package io.magentys.donut.transformers.cucumber

import java.io.File

import io.magentys.donut.DonutTestData
import io.magentys.donut.gherkin.model
import io.magentys.donut.gherkin.model.Metrics
import io.magentys.donut.gherkin.processors.JSONProcessor
import org.scalatest.{FlatSpec, Matchers}

class CucumberTransformerTest extends FlatSpec with Matchers {

  val rootDir = List("src", "test", "resources", "samples-1").mkString("", File.separator, File.separator)
  val values = JSONProcessor.loadFrom(new File(rootDir))
  val features = values.right.flatMap {e => CucumberTransformer.transform(e, DonutTestData.statusConfiguration) }

  behavior of "CucumberAdaptor"

  it should "transoform all files json values to list of Features" in {
    features.fold(
      e => fail(e),
      f => {
        f.size shouldBe 9
        f(0).name shouldBe "Google Journey Performance"
        f(1).name shouldBe "Google search"
        f(2).name shouldBe "Offset Actions"
        f(3).name shouldBe "Input Actions"
        f(4).name shouldBe "Mouse actions"
        f(5).name shouldBe "Performance"
        f(6).name shouldBe "Select"
        f(7).name shouldBe "Switch to window"
        f(8).name shouldBe "Tables"
      }
    )
  }

//  it should "return empty list if there are no features" in {
//    CucumberTransformer.transform(List.empty, DonutTestData.statusConfiguration) shouldEqual List.empty
//  }
//
//  it should "enhance scenarios with extra values" in {
//    val enhancedScenarios = features.right.map(f => f.map(e => e.scenarios)).fold(
//      fail,
//      s => {
//        s(0).status.status shouldEqual(false)
//        s(0).status.statusStr shouldEqual("failed")
//        s(0).featureName shouldEqual("Google Journey Performance")
//        s(0).featureIndex shouldEqual("10000")
//        s(0).duration.duration shouldEqual(7984105000L)
//        s(0).duration.durationStr shouldEqual("7 secs and 984 ms")
//        s(0).screenshotsSize shouldEqual(1)
//        s(0).screenshotStyle shouldEqual("")
//        // enhancedScenarios(0).screenshotIDs shouldEqual(embeddings(0).data.hashCode.toString)
//        s(0).background shouldEqual(None)
//      }
//    )
//
//  }
//
//  it should "enhance steps with user friendly duration" in {
//    val enhancedSteps = features.flatMap(f => f.scenarios).flatMap(e => e.steps)
//    enhancedSteps(0).duration.durationStr shouldEqual("7 secs and 977 ms")
//    enhancedSteps(1).duration.durationStr shouldEqual("6 ms")
//  }
//
//  behavior of "CucumberAdaptor units"
//
//  it should "mapToDonutFeatures" in {
//    val originalFeatures: List[Feature] = CucumberTransformer.loadCukeFeatures(values)
//    val generatedFeatures = CucumberTransformer.mapToDonutFeatures(originalFeatures, DonutTestData.statusConfiguration)
//    generatedFeatures.size shouldEqual originalFeatures.size
//
//    for {
//      o <- originalFeatures
//      g <- generatedFeatures
//    } yield if(o.name == g.name) {
//        o.elements.size shouldBe g.scenarios.size
//        g.index.toInt shouldBe >= (10000)
//    }
//  }
//
//  it should "mapToDonutFeature" in {
//    val originalFeatures: List[Feature] = CucumberTransformer.loadCukeFeatures(values)
//    val feature: model.Feature = CucumberTransformer.mapToDonutFeature(originalFeatures(0), "10000", DonutTestData.statusConfiguration)
//    feature.isInstanceOf[model.Feature] shouldBe true
//    feature.duration.duration shouldEqual (7984105000L)
//    feature.duration.durationStr shouldEqual ("7 secs and 984 ms")
//    feature.status.status shouldEqual (false)
//    feature.status.statusStr shouldEqual ("failed")
//    feature.htmlFeatureTags shouldEqual (List("google", "performance"))
//    feature.scenarioMetrics shouldEqual (Metrics(1, 0, 1))
//    feature.stepMetrics shouldEqual (Metrics(0, 0, 0, 0, 0, 0, 0))
//    feature.index shouldEqual "10000"
//  }

//  it should "mapToDonutScenario" in {
//
//  }
//
//  it should "mapToDonutStep" in {
//
//  }
//
//  it should "donutFeatureStatus" in {
//
//  }
//
//  it should "donutScenarioScreenshots" in {
//
//  }
//
//  it should "donutScenarioDuration" in {
//
//  }
//
//  it should "donutScenarioStatus" in {
//
//  }
}
