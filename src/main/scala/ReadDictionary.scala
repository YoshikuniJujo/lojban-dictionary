package iocikun.juj.lojban.dictionary

import _root_.java.io.{BufferedReader, InputStreamReader, FileNotFoundException}
import _root_.scala.xml.{XML, Node}

import _root_.android.content.res.AssetManager
import _root_.android.content.SharedPreferences

class ReadDictionary(asset: AssetManager, sp: SharedPreferences) {
	val initialString = "Hello!\nThis is Lojban dictionary!"

	def lojToEn(loj: String): String = {
		val en = getEn(loj)
		if (en != null) leStr(en) else loj + ": no result"
	}

	def enToLoj(en: String): String = {
		val loj = getLoj(en)
		if (loj != null) elStr(loj) else en + ": no result"
	}

	def rafsiToLoj(rafsi: String): String = {
		val en = getRafsi(rafsi)
		if (en != null) leStr(en) else rafsi + ":no result"
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

	def getDef(file_name: String, tag: String,
		filter: (String, Node) => Boolean, trgt: String): Node = {
		val file = new BufferedReader(new InputStreamReader(
			asset.open(file_name), "UTF-8"))
		val xml = XML.load(file)
		for (definition <- xml \ tag) {
			if (filter(trgt, definition)) return definition
		}
		return null
	}

	def filter(trgt: String, definition: Node) =
		(definition \ "@word").toString == trgt

	def filterR(trgt: String, definition: Node) = {
		val list = for (r <- definition \ "rafsi") yield r.text
		list contains trgt
	}

	def getEn(loj: String): Node = {
		if (loj == "") return null

		val dir = "loj" + (if (sp.contains("lang"))
			sp.getString("lang", "en") else "en") + "/"
		val fn = loj.substring(0, 1) + ".xml"

		for (d <- List(dir, "lojen/")) {
			try {
				val ret1 = getDef(d + fn, "valsi", filter, loj)
				if (ret1 != null) return ret1
			} catch {
			case ex: FileNotFoundException =>
				val ret2 = getDef(d + "rest.xml", "valsi",
					filter, loj)
				if (ret2 != null) return ret2
			}
		}

		return null
	}

	def getLoj(en: String): Node = {
		if (en == "") return null

		val dir = (if (sp.contains("lang")) sp.getString("lang", "en")
			else "en") + "loj/"
		val fn = en.substring(0, 1) + ".xml"

		for (d <- List(dir, "enloj/")) {
			try {
				val ret1 = getDef(d + fn, "nlword", filter, en)
				if (ret1 != null) return ret1
			} catch {
			case ex: FileNotFoundException =>
				val ret2 = getDef(d + "rest.xml", "nlword",
					filter, en)
				if (ret2 != null) return ret2
			}
		}

		return null
	}

	def getRafsi(rafsi: String): Node = {
		val fn = if (sp.contains("lang"))
				sp.getString("lang", "en") + ".xml"
			else	"en.xml"

		for (f <- List(fn, "en.xml")) {
			val ret1 = getDef("gismu/" ++ f, "valsi", filterR, rafsi)
			if (ret1 != null) return ret1
			val ret2 = getDef("cmavo/" ++ f, "valsi", filterR, rafsi)
			if (ret2 != null) return ret2
		}

		return null
	}

}
