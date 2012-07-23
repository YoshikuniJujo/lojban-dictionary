package iocikun.juj.lojban.dictionary

import _root_.android.app.Activity
import _root_.android.content.Intent
import _root_.android.os.Bundle
import _root_.android.view.View
import _root_.android.view.View.OnClickListener
import _root_.android.widget.TextView
import _root_.android.widget.Button
import _root_.android.widget.EditText
import _root_.android.view.Menu
import _root_.android.view.MenuItem
import _root_.android.widget.Toast

import _root_.android.preference.PreferenceManager

import iocikun.juj.lojban.dictionary.ReadDictionary

class LojbanDictionary extends Activity with TypedActivity {

	lazy val readDic: ReadDictionary = new ReadDictionary(getAssets(), sp)

	lazy val editText = findView(TR.input).asInstanceOf[EditText]
	lazy val textView = findView(TR.textview).asInstanceOf[TextView]
	lazy val lojen = findView(TR.lojen).asInstanceOf[Button]
	lazy val enloj = findView(TR.enloj).asInstanceOf[Button]
	lazy val rafsi = findView(TR.rafsi).asInstanceOf[Button]

	lazy val sp = PreferenceManager.getDefaultSharedPreferences(this)

	override def onCreate(bundle: Bundle) {
		super.onCreate(bundle)
		setContentView(R.layout.main)

		lojen.setOnClickListener(new View.OnClickListener() {
			def onClick(v: View) {
				var str = editText.getText.toString()
				textView.setText(readDic.lojToEn2(str))
			}
		})

		enloj.setOnClickListener(new View.OnClickListener() {
			def onClick(v: View) {
				var str = editText.getText.toString()
				textView.setText(readDic.enToLoj2(str))
			}
		})

		rafsi.setOnClickListener(new View.OnClickListener() {
			def onClick(v: View) {
				var str = editText.getText.toString()
				textView.setText(readDic.rafsiToLoj(str))
			}
		})


//		readDic = new ReadDictionary(getAssets())
		findView(TR.textview).setText(readDic.initialString)
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
