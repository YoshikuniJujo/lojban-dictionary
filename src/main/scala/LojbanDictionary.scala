package iocikun.juj.lojban.dictionary

import _root_.android.app.Activity
import _root_.android.os.Bundle
import _root_.android.view.View
import _root_.android.view.View.OnClickListener
import _root_.android.widget.TextView
import _root_.android.widget.Button
import _root_.android.widget.EditText

import iocikun.juj.lojban.dictionary.ReadDictionary

class LojbanDictionary extends Activity with TypedActivity {

	var readDic: ReadDictionary = null;

	lazy val editText = findView(TR.input).asInstanceOf[EditText]
	lazy val textView = findView(TR.textview).asInstanceOf[TextView]
	lazy val lojen = findView(TR.lojen).asInstanceOf[Button]
	lazy val enloj = findView(TR.enloj).asInstanceOf[Button]
	lazy val rafsi = findView(TR.rafsi).asInstanceOf[Button]

	override def onCreate(bundle: Bundle) {
		super.onCreate(bundle)
		setContentView(R.layout.main)

		lojen.setOnClickListener(new View.OnClickListener() {
			def onClick(v: View) { clickFun() }
		})

		enloj.setOnClickListener(new View.OnClickListener() {
			def onClick(v: View) { enlojFun() }
		})

		rafsi.setOnClickListener(new View.OnClickListener() {
			def onClick(v: View) { rafsiFun() }
		})


		readDic = new ReadDictionary(getAssets())
		findView(TR.textview).setText(readDic.initialString)
	}

	def clickFun() {
		var str = editText.getText.toString()
		textView.setText(readDic.lojToEn(str))
	}

	def enlojFun() {
		var str = editText.getText.toString()
		textView.setText(readDic.enToLoj(str))
	}

	def rafsiFun() {
		var str = editText.getText.toString()
		textView.setText(readDic.rafsiToLoj(str))
	}

}
