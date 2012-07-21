package iocikun.juj.lojban.dictionary

import _root_.java.io.BufferedReader
import _root_.java.io.InputStreamReader
import _root_.scala.xml.{XML, Node}

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

	lazy val englishFile = new BufferedReader(new InputStreamReader(
		asset.open("lojban_en.xml") , "UTF-8"))
	lazy val englishXml = XML.load(englishFile)

	def getEn(loj: String): Node = {
		for (dir <- englishXml \ "direction") {
			if ((dir \ "@from").toString == "lojban" &&
				(dir \ "@to").toString == "English")
				for (valsi <- dir \ "valsi") {
					if ((valsi \ "@word").toString == loj)
						return valsi
				}
		}
		return null
	}

	def getLoj(en: String): Node = {
		for (dir <- englishXml \ "direction") {
			if ((dir \ "@from").toString == "English" &&
				(dir \ "@to").toString == "lojban")
				for (nlword <- dir \ "nlword") {
					if ((nlword \ "@word").toString == en)
						return nlword
				}
		}
		return null
	}

	def enToLoj2(en: String): String = {
		val loj = getLoj(en)
		if (loj != null) elStr(loj)
		else en + ": no result"
	}

	def leStr(valsi: Node): String = {
		var rafsiStr = ""
		for (r <- valsi \ "rafsi") rafsiStr += "\nrafsi: " + r.text
		return valsi \ "@word" + "\ntype: " + valsi \ "@type" + rafsiStr +
			"\ndefinition: " + (valsi \ "definition").text +
			"\nnotes: " + (valsi \ "notes").text
	}

	def elStr(nlword: Node): String = {
		return nlword \ "@valsi" + "\nword: " + nlword \ "@word" +
			"\nsense: " + nlword \ "@sense"
	}

	def lojToEn2(loj: String): String = {
		val en = getEn(loj)
		if (en != null) leStr(en)
		else loj + ": no result"
	}

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
