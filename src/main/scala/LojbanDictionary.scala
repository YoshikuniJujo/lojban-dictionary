package iocikun.juj.lojban.dictionary

import _root_.java.io.FileReader
import _root_.java.io.BufferedReader
import _root_.java.io.InputStreamReader
import _root_.scala.util.control.Breaks._

import _root_.android.app.Activity
import _root_.android.os.Bundle
import _root_.android.view.View
import _root_.android.view.View.OnClickListener
import _root_.android.widget.TextView
import _root_.android.widget.Button
import _root_.android.widget.EditText
import _root_.android.content.res.AssetManager

class LojbanDictionary extends Activity with TypedActivity {

	lazy val editText = findView(TR.input).asInstanceOf[EditText]
	lazy val textView = findView(TR.textview).asInstanceOf[TextView]
	lazy val button = findView(TR.button).asInstanceOf[Button]
	lazy val reader = new BufferedReader(new InputStreamReader(
		getAssets().open("gismu.txt"), "UTF-8"));
	var list: List[String] = List()

	override def onCreate(bundle: Bundle) {
		super.onCreate(bundle)
		setContentView(R.layout.main)

		var line = ""
		while({line = reader.readLine(); line != null}) {
			list ::= line
		}

		button.setOnClickListener(new View.OnClickListener() {
			def onClick(v: View) {
				clickFun()
			}
		})

		findView(TR.textview).setText("Hello! It's lojban dictinary")
	}

	def fun(n: Int): Int = if (n == 0) 1 else n * fun(n - 1)

	def clickFun() {
		textView.setText(editText.getText.toString() +
			reader.readLine())
		var str = editText.getText.toString()
		var line = ""
		for(line <- list) {
			if (line.startsWith(" " + str) ||
				line.slice(20, 100).startsWith(str)) {
				textView.setText(line)
			}
		}
	}

}
