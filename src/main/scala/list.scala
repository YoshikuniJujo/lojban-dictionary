package iocikun.juj.lojban.dictionary

object TestList {
	def main(args: Array[String]) {
		val myList = new MyList[String]("")
		myList.add("hello")
		myList.backward
		myList.add("world")
		myList.backward
		myList.add("!")
		println(myList)
		println(myList toLines)

		val testList = Tools stringToMyList("hello\nworld\n\n!")
		println(testList)
		println(testList toLines)

		val testBSStr = "t hello\nf world\n\nt good\nf bye\nt world\n"
		val testBSList = Tools getBoolStrings(testBSStr)
		println(testBSList)
		println(testBSList toLines)
		println(Tools showBoolStrings(testBSList))
	}
}

object Tools {
	def stringToMyList(str: String): MyList[String] = {
		val list = str split('\n') toList
		val l1 = list takeWhile (_ != "")
		val l2 = list dropWhile (_ != "") tail
		val ret = new MyList[String]("", l1, l2)
		return ret
	}

	def getBoolStrings(str: String): MyList[(Boolean, String)] = {
		val list = str split('\n') toList
		val l1 = list takeWhile (_ != "") map
			(l => readBoolString(l split ('@') toList))
		val l2 = tail(list dropWhile (_ != "")) map
			(l => readBoolString(l split ('@') toList))
		val ret = new MyList[(Boolean, String)]((false, ""), l1, l2)
		return ret
	}

	def tail(l: List[String]): List[String] =
		if (l isEmpty) List() else l.tail

	def showBoolStrings(ml: MyList[(Boolean, String)]): String = {
		val (l1, l2) = ml getMembers
		val ret = (l1 map (l => showBoolString(l)) mkString("\n")) + "\n\n" +
			(l2 map (l => showBoolString(l)) mkString("\n"))
		if ((l1 isEmpty) && (l2 isEmpty)) {
			return ""
		} else {
			return ret
		}
	}

	def readBoolString(p: List[String]): (Boolean, String) = p match {
		case "t" :: str :: _ => (true, str)
		case "f" :: str :: _ => (false, str)
		case _ => (false, "")
	}

	def showBoolString(bs: (Boolean, String)): String = bs match {
		case (true, str) => "t@" + str
		case (false, str) => "f@" + str
	}
}

class MyList[T](default: T) {
	var list1: List[T] = List()
	var list2: List[T] = List()

	def this(default: T, l1: List[T], l2: List[T]) {
		this(default)
		list1 = l1
		list2 = l2
	}

	def getMembers() = (list1, list2)

	def add(elem: T) {
		if ((list1 == Nil || list1(0) != elem) &&
			(list2 == Nil || list2(0) != elem))
			list1 = elem :: list1
	}

	def get = if (list1 == Nil) default else list1(0)

	def forwardable = list2 match {
		case Nil => false
		case lst => true }

	def forward = list2 match {
		case h :: t => 
			list1 = h :: list1
			list2 = t
			(t != Nil, true)
		case Nil => (false, false) }

	def backable = list1 match {
		case h :: Nil => false
		case Nil => false
		case lst => true }

	def backward = list1 match {
		case h :: Nil => (false, false)
		case h :: t =>
			list2 = h :: list2
			list1 = t
			(t.size > 1, true)
		case Nil => (false, false) }

	def toLines = (list1 ::: "" :: list2).mkString("\n")

	def clear {
		list1 = List()
		list2 = List()
	}

	override def toString() = (list1.reverse ::: list2).toString
}
