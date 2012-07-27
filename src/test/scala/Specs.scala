import iocikun.juj.lojban.dictionary
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.FunSpec
import iocikun.juj.lojban.dictionary.ReadDictionaryGen

import scala.xml.{XML, Node}
import java.io.{BufferedReader, InputStreamReader, FileInputStream, File}

class Specs extends FunSpec with ShouldMatchers {

	val readDic = new ReadDictionaryGen(getDef, () => "ja")

	describe("a spec") {
		it("should do something") {
		}

		it("should be klama") {
			readDic.lojToEn("klama")._1 should be ("klama")
			readDic.enToLoj("来る")(0)._1 should be ("klama")
			readDic.enToLoj("come")(1)._1 should be ("klama")
			readDic.rafsiToLoj("kla")._1 should be ("klama")
		}

		it("should be no such valsi") {
			readDic.lojToEn("hoge")._2 should be (
				"hoge: no such valsi in the dictionary")
		}
	}

	def getDef(file_name: String, tag: String,
		filter: (String, Node) => Boolean, trgt: String): List[Node] = {
		val dir = "src/main/assets/"
		var list: List[Node] = List()
		val file = new BufferedReader(new InputStreamReader(
			new FileInputStream(new File(dir + file_name)), "UTF-8"))
		val xml = XML.load(file)
		for (definition <- xml \ tag) {
			if (filter(trgt, definition)) list = definition :: list
		}
		return list
	}
}
