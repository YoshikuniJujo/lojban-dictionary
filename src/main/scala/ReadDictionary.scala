package iocikun.juj.lojban.dictionary

import _root_.java.io.BufferedReader
import _root_.java.io.InputStreamReader

import _root_.android.content.res.AssetManager

class ReadDictionary(asset: AssetManager) {
	val initialString = "Hello!\nThis is Lojban dictionary!"

	lazy val reader = new BufferedReader(new InputStreamReader(
		asset.open("gismu.txt"), "UTF-8"));
	lazy val cmavo = new BufferedReader(new InputStreamReader(
		asset.open("cmavo.txt"), "UTF-8"));

	var list: List[String] = List()
	var clist: List[String] = List()
	var line = ""

	while ({line = reader.readLine(); line != null}) { list ::= line }
	line = ""
	while ({line = cmavo.readLine(); line != null}) { clist ::= line }

	def lojToEn(loj: String): String = {
		var line = ""
		for (line <- list){
			if (line.slice(1, 100).startsWith(loj)) {
				return line
			}
		}
		for (line <- clist) {
			if (line.slice(1, 100).startsWith(loj)) {
				return line
			}
		}
		return loj + ": no result"
	}

	def enToLoj(en: String): String = {
		var line = ""
		for (line <- list) {
			if (line.slice(20, 100).startsWith(en)) {
				return line
			}
		}
		for (line <- clist) {
			if (line.slice(20, 100).startsWith(en)) {
				return line
			}
		}
		return en + ": no result"
	}

	def rafsiToLoj(rafsi: String): String = {
		var line = ""
		for (line <- list) {
			if (line.slice(7, 10).startsWith(rafsi) ||
				line.slice(11, 14).startsWith(rafsi) ||
				line.slice(15, 19).startsWith(rafsi)) {
				return line
			}
		}
		return rafsi + ": no result"
	}
}
