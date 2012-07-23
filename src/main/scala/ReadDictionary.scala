package iocikun.juj.lojban.dictionary

import _root_.java.io.{BufferedReader, InputStreamReader, FileNotFoundException}
import _root_.scala.xml.{XML, Node}

import _root_.android.content.res.AssetManager
import _root_.android.content.SharedPreferences

class ReadDictionary(asset: AssetManager, sp: SharedPreferences) {
	val initialString = "Hello!\nThis is Lojban dictionary!"

	def getRafsi(rafsi: String): Node = {
		val lang = if (sp.contains("lang")) sp.getString("lang", "en")
			else "en"
		val file_name = "gismu/" ++ lang ++ ".xml"

		val file = new BufferedReader(new InputStreamReader(
		asset.open(file_name) , "UTF-8"))
		val xml = XML.load(file)

		for (valsi <- xml \ "valsi") {
			val list = for (r <- valsi \ "rafsi") yield r.text
			if (list contains rafsi) return valsi
		}

		val file_name_en = "gismu/en.xml"

		val file_en = new BufferedReader(new InputStreamReader(
		asset.open(file_name_en) , "UTF-8"))
		val xml_en = XML.load(file_en)

		for (valsi <- xml_en \ "valsi") {
			val list = for (r <- valsi \ "rafsi") yield r.text
			if (list contains rafsi) return valsi
		}

		return null
	}

	def getEn(loj: String): Node = {

		if (loj == "") return null

		val lang = if (sp.contains("lang")) sp.getString("lang", "en")
			else "en"
		val dir = "loj" + lang + "/"

		try {
			lazy val file = new BufferedReader(new InputStreamReader(
				asset.open(dir + loj.substring(0, 1) + ".xml"),
					"UTF-8"))
			lazy val xml = XML.load(file)

			for (valsi <- xml \ "valsi") {
				if ((valsi \ "@word").toString == loj) return valsi
			}
		} catch {
			case ex: FileNotFoundException =>
		}

		val file = new BufferedReader(new InputStreamReader(
		asset.open(dir + "rest.xml") , "UTF-8"))
		val xml = XML.load(file)

		for (valsi <- xml \ "valsi") {
			if ((valsi \ "@word").toString == loj) return valsi
		}

		val diren = "lojen/"

		try {
			lazy val file = new BufferedReader(new InputStreamReader(
				asset.open(diren + loj.substring(0, 1) + ".xml"),
					"UTF-8"))
			lazy val xml = XML.load(file)

			for (valsi <- xml \ "valsi") {
				if ((valsi \ "@word").toString == loj) return valsi
			}
		} catch {
			case ex: FileNotFoundException =>
		}

		val fileen = new BufferedReader(new InputStreamReader(
		asset.open(diren + "rest.xml") , "UTF-8"))
		val xmlen = XML.load(fileen)

		for (valsi <- xmlen \ "valsi") {
			if ((valsi \ "@word").toString == loj) return valsi
		}

		return null
	}

	def getLoj(en: String): Node = {

		if (en == "") return null

		val lang = if (sp.contains("lang")) sp.getString("lang", "en")
			else "en"
		val dir = lang + "loj/"

		try {
			lazy val file = new BufferedReader(new InputStreamReader(
				asset.open(dir + en.substring(0, 1) + ".xml"),
					"UTF-8"))
			lazy val xml = XML.load(file)

			for (nlword <- xml \ "nlword") {
				if ((nlword \ "@word").toString == en) return nlword
			}
		} catch {
			case ex: FileNotFoundException =>
		}

		val file = new BufferedReader(new InputStreamReader(
		asset.open(dir + "rest.xml"), "UTF-8"))
		val xml = XML.load(file)

		for (nlword <- xml \ "nlword") {
			if ((nlword \ "@word").toString == en) return nlword
		}

		val diren = "enloj/"

		try {
			lazy val file = new BufferedReader(new InputStreamReader(
				asset.open(diren + en.substring(0, 1) + ".xml"),
					"UTF-8"))
			lazy val xml = XML.load(file)

			for (nlword <- xml \ "nlword") {
				if ((nlword \ "@word").toString == en) return nlword
			}
		} catch {
			case ex: FileNotFoundException =>
		}

		val fileen = new BufferedReader(new InputStreamReader(
		asset.open(diren + "rest.xml") , "UTF-8"))
		val xmlen = XML.load(fileen)

		for (nlword <- xmlen \ "nlword") {
			if ((nlword \ "@word").toString == en) return nlword
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

	def rafsiToLoj2(rafsi: String): String = {
		val en = getRafsi(rafsi)
		if (en != null) leStr(en)
		else rafsi + ":no result"
	}

}
