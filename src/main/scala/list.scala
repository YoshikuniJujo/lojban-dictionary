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

	def add(elem: T) {
		if ((list1 == Nil || list1(0) != elem) &&
			(list2 == Nil || list2(0) != elem))
			list1 = elem :: list1
	}

	def get = if (list1 == Nil) default else list1(0)

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

	override def toString() = (list1.reverse ::: list2).toString
}
