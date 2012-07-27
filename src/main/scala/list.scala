package iocikun.juj.lojban.dictionary

object TestList {
	def main(args: Array[String]) {
		val myList = new MyList[String]("")
		myList.add("hello")
		myList.backward
		myList.add("world")
		println(myList)
	}
}

class MyList[T](default: T) {
	var list1: List[T] = List()
	var list2: List[T] = List()

	def add(elem: T) { list1 = elem :: list1 }

	def get = if (list1 == Nil) default else list1(0)

	def forward = list2 match {
		case h :: t => { list1 = h :: list1; list2 = t; t != Nil }
		case Nil => false }

	def backward = list1 match {
		case h :: Nil => false
		case h :: t => { list2 = h :: list2; list1 = t; t != Nil }
		case Nil => false }

	override def toString() = (list1.reverse ::: list2).toString
}
