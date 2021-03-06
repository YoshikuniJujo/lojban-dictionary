import sbt._

import Keys._
import AndroidKeys._

object General {
  val settings = Defaults.defaultSettings ++ Seq (
    name := "Lojban Dictionary",
    version := "0.18.28",
    versionCode := 84,
    scalaVersion := "2.9.1",
    platformName in Android := "android-17"
  )

  val proguardSettings = Seq (
    useProguard in Android := true
  )

  lazy val fullAndroidSettings =
    General.settings ++
    AndroidProject.androidSettings ++
    TypedResources.settings ++
    proguardSettings ++
    AndroidManifestGenerator.settings ++
    AndroidMarketPublish.settings ++ Seq (
      keyalias in Android := "yoshikuni",
      libraryDependencies += "org.scalatest" %% "scalatest" % "1.8.RC1" % "test"
    )
}

object AndroidBuild extends Build {
  lazy val main = Project (
    "LojbanDictionary",
    file("."),
    settings = General.fullAndroidSettings
  )

  lazy val tests = Project (
    "tests",
    file("tests"),
    settings = General.settings ++
               AndroidTest.androidSettings ++
               General.proguardSettings ++ Seq (
      name := "Lojban DictionaryTests"
    )
  ) dependsOn main
}
