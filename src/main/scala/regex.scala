package iocikun.juj.lojban.dictionary

import scala.util.matching

object TestRegex {
	def main(args: Array[String]) {
		val mr = new MyRegex
		println("hello")
		println(mr.replace("x_1"))
		println(mr.replace("x_{1}"))
		println(mr.rep("hoge_1$x_{1}=y_2$hoge_2"))
	}
}

class MyRegex() {
	val reg = """([a-z]+)_([0-9]|\{([0-9])\})""" r

	def rep(str: String) = repArray(str.split("\\$"))

	def replace(str: String) = {
		reg.replaceAllIn(str, replaceFun _)
	}

	def replaceFun(m: matching.Regex.Match):String = {
		m.group(1) + "<SMALL>" +
		(if (m.group(3) == null) m.group(2) else m.group(3)) +
		"</SMALL>"
	}

	def repArray(a: Array[String]): String = {
		var ret = ""
		var i = 0
		for (x <- a) {
			i += 1
			if (i % 2 == 0) ret += replace(x)
			else ret += x
		}
		ret
	}
}
