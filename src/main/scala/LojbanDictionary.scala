package iocikun.juj.lojban.dictionary


import _root_.android.app.Activity
import _root_.android.content.Intent
import _root_.android.os.Bundle
import _root_.android.view.View
import _root_.android.view.View.OnClickListener
import _root_.android.widget.TextView
import _root_.android.widget.Button
import _root_.android.widget.EditText
import _root_.android.widget.LinearLayout
import _root_.android.view.Menu
import _root_.android.view.MenuItem
import _root_.android.widget.Toast

import _root_.android.view.Window

import _root_.android.preference.PreferenceManager

import _root_.android.text.Html

import iocikun.juj.lojban.dictionary.ReadDictionary

class LojbanDictionary extends Activity with TypedActivity {

	lazy val readDic: ReadDictionary = new ReadDictionary(getAssets(), sp)

	lazy val editText = findView(TR.input).asInstanceOf[EditText]
	lazy val textView = findView(TR.textview).asInstanceOf[TextView]
	lazy val lojen = findView(TR.lojen).asInstanceOf[Button]
	lazy val enloj = findView(TR.enloj).asInstanceOf[Button]
	lazy val rafsi = findView(TR.rafsi).asInstanceOf[Button]
	lazy val listview = findView(TR.listview).asInstanceOf[LinearLayout]

	lazy val sp = PreferenceManager.getDefaultSharedPreferences(this)

	override def onCreate(bundle: Bundle) {
		super.onCreate(bundle)

		requestWindowFeature(Window.FEATURE_NO_TITLE)

		setContentView(R.layout.main)

		lojen.setOnClickListener(new View.OnClickListener() {
			def onClick(v: View) {
				val str = editText.getText.toString()
				val result = readDic.lojToEn(str)
				textView.setText(
					Html.fromHtml(result._1))
				mkLinks(result._2)
			}
		})

		enloj.setOnClickListener(new View.OnClickListener() {
			def onClick(v: View) {
				var str = editText.getText.toString()
				textView.setText(
					Html.fromHtml(readDic.enToLoj(str)))
			}
		})

		rafsi.setOnClickListener(new View.OnClickListener() {
			def onClick(v: View) {
				val str = editText.getText.toString()
				val result = readDic.rafsiToLoj(str)
				textView.setText(
					Html.fromHtml(result._1))
				mkLinks(result._2)
			}
		})


		findView(TR.textview).setText(readDic.initialString)

//		mkLinks(List("gerku", "prami", "klama"))
	}

	def mkLinks(list: List[String]) {
		listview.removeAllViews
		for (valsi <- list) {
			val tv = new TextView(this)
			tv.setTextSize(30)
			tv.setText(valsi)
			tv.setClickable(true)
			tv.setOnClickListener(new View.OnClickListener() {
				def onClick(v: View) {
					val result = readDic.lojToEn(valsi)
					textView.setText(Html.fromHtml(result._1))
					mkLinks(result._2)
				}
			})
			listview.addView(tv)
		}
	}

	override def onResume {
		super.onResume
		if (sp.contains("lang")) {
			lojen.setText("loj -> " + sp.getString("lang", ""))
			enloj.setText(sp.getString("lang", "") + " -> loj")
		}
	}

	override def onCreateOptionsMenu(menu: Menu): Boolean = {
		menu.add(Menu.NONE, 0, 0, "Select Lang")
		return super.onCreateOptionsMenu(menu)
	}

	override def onOptionsItemSelected(item: MenuItem): Boolean = {
		item.getItemId() match {
		case 0 =>
			val intent = new Intent(this, classOf[Preference])
			startActivity(intent)
		}
		return true
	}
}
