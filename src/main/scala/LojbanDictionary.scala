package iocikun.juj.lojban.dictionary


import _root_.android.app.Activity
import _root_.android.os.Bundle
import _root_.android.content.Intent
import _root_.android.content.pm.ActivityInfo
import _root_.android.view.View
import _root_.android.view.View.OnClickListener
import _root_.android.view.Window
import _root_.android.view.Menu
import _root_.android.view.MenuItem
import _root_.android.widget.TextView
import _root_.android.widget.AutoCompleteTextView
import _root_.android.widget.ArrayAdapter
import _root_.android.widget.EditText
import _root_.android.widget.Button
import _root_.android.widget.LinearLayout
import _root_.android.preference.PreferenceManager
import _root_.android.text.Html

import _root_.android.util.Log
import _root_.android.widget.Toast

import _root_.android.view.KeyEvent

import _root_.android.app.SearchManager

import _root_.android.content.SearchRecentSuggestionsProvider
import _root_.android.provider.SearchRecentSuggestions

import _root_.android.content.Context
import _root_.android.content.SharedPreferences

import _root_.com.android.vending.billing.IInAppBillingService
import _root_.android.content.ComponentName
import _root_.android.content.ServiceConnection
import _root_.android.os.IBinder
import _root_.java.util.ArrayList
import scala.collection.JavaConversions._
import _root_.org.json.JSONObject
import _root_.android.app.PendingIntent

class MySuggestionProviderClass extends SearchRecentSuggestionsProvider {
	setupSuggestions("lojban dictionary", 1);
}

class SearchActivity extends Activity with TypedActivity {
	lazy val sp = PreferenceManager getDefaultSharedPreferences this
	lazy val dic = new ReadDictionary(getAssets(), sp)

	lazy val test = findView(TR.test)

	lazy val field = findView(TR.fieldS)
	lazy val valsi = findView(TR.valsiS)
	lazy val velcki = findView(TR.velckiS)

	override def onCreate(savedInstanceState: Bundle) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE)
		setContentView(R.layout.search)

		val intent = getIntent()
		var action: String = null
		var query: String = null
		if (intent != null) action = intent.getAction()
		if (action != null) {
			test.setText(action)
			if (action.equals(Intent.ACTION_SEARCH) ||
				action.equals(Intent.ACTION_VIEW))
				query = intent.getStringExtra(SearchManager.QUERY);
		}

		if (query != null) {
			val result1 = dic lojToEn query.trim.toLowerCase
			val result2 = dic enToLoj query.trim.toLowerCase
			val result3 = dic rafsi query.trim.toLowerCase
			if (result1._1 != "") {
				putDef(result1)
			} else if (result2 != Nil && result2(0)._1 != "") {
				mkEnLoj(result2)
			} else {
				putDef(result3)
			}

			val suggestions = new SearchRecentSuggestions(this,
				"lojban dictionary", 1);
			suggestions.saveRecentQuery(query, null);

//			Toast.makeText(this, result3._2, Toast.LENGTH_LONG).show();
		}
	}

	def putDef(result: (String, String, List[String])) {
		valsi.setText(result._1)
		velcki.setText(Html.fromHtml(result._2))
		field.removeAllViews
		field.addView(valsi)
		field.addView(velcki)
		mkLinks(result._3)
	}

	def mkLinks(list: List[String]) {
		for (v <- list) {
			val tv = new TextView(this)
			tv.setTextSize(30)
			tv.setText(v)
			tv.setClickable(true)
			tv.setOnClickListener(new View.OnClickListener() {
				def onClick(_v: View) {
					val result = dic.lojToEn(v)
					putDef(result)
				}
			})
			field.addView(tv)
		}
	}

	def mkEnLoj(list: List[(String, String)]) {
		valsi.setText("")
		velcki.setText("")
		field.removeAllViews
		for (result <- list) {
			val tv1 = new TextView(this)
			val tv2 = new TextView(this)
			tv1.setTextSize(30)
			tv1.setText(result._1)
			tv1.setOnClickListener(new View.OnClickListener() {
				def onClick(v: View) {
		Log.d("LojbanDictionary", "onClick start")
					val result2 = dic.lojToEn(result._1)
		Log.d("LojbanDictionary", result2._1)
					putDef(result2)
		Log.d("LojbanDictionary", "onClick end")
				}
			})
			tv1.setClickable(true)
			tv2.setText(Html.fromHtml(result._2))
			field.addView(tv1)
			field.addView(tv2)
		}
	}
}

class LojbanDictionary extends Activity with TypedActivity {

	lazy val spr = getSharedPreferences("content01", Context.MODE_PRIVATE);
	lazy val spre = spr edit

	lazy val sp = PreferenceManager getDefaultSharedPreferences this
	lazy val dic = new ReadDictionary(getAssets(), sp)

	lazy val input = findView(TR.input)
	lazy val lojen = findView(TR.lojen)
	lazy val enloj = findView(TR.enloj)
	lazy val rafsi = findView(TR.rafsi)
	lazy val field = findView(TR.field)
	lazy val valsi = findView(TR.valsi)
	lazy val velcki = findView(TR.velcki)

	lazy val back = findView(TR.back)
	lazy val forward = findView(TR.forward)

	lazy val historyStr = spr.getString("history", "")
//	lazy val history = new MyList(false, "")
	lazy val history: MyList[(Boolean, String)] = Tools getBoolStrings(historyStr) // ("f cmene\n\nf lujvo\n") // new MyList(false, "")

	lazy val allwords = dic.allwords
	lazy val adapter = new ArrayAdapter[String](
		this, android.R.layout.simple_dropdown_item_1line, allwords)

	def resetCompletion() {
		val allwords = dic.allwords
		val adapter = new ArrayAdapter[String](
			this, android.R.layout.simple_dropdown_item_1line, allwords)
		input.setAdapter(adapter)
	}

	var mService: IInAppBillingService = null
	val mServiceConn = new ServiceConnection () {
		override def onServiceDisconnected(
			name: ComponentName) {
			mService = null
		}
		override def onServiceConnected(
			name: ComponentName,
			service: IBinder) {
			mService = IInAppBillingService.Stub.
				asInterface(service)
		}
	}

	override def onDestroy() {
		super.onDestroy();
		if (mServiceConn != null) {
			unbindService(mServiceConn)
		}
	}

	override def onCreate(bundle: Bundle) {
		Log.d("LojbanDictionary", "onCreate")
		super.onCreate(bundle)
		requestWindowFeature(Window.FEATURE_NO_TITLE)
		setContentView(R.layout.main)
		input.setAdapter(adapter)
		bindService(new
			Intent("com.android.vending.billing.InAppBillingService.BIND"),
				mServiceConn, Context.BIND_AUTO_CREATE)

/*
		val test = spr.getInt("DATA1", 0);
		spre putInt("DATA1", test + 1);
		spre commit
*/
		Log.d("LojbanDictionary", "history = " + history.toString)

		val (b, v) = history.get
		putDef(("", "coi rodo mi jbovlaste", List()))
		if (v == "") {
			putDef(("", "coi rodo mi jbovlaste", List()))
		} else {
			if (b) {
				val list = dic enToLoj v
				mkEnLoj(list)
			} else {
				val result = dic lojToEn v
				putDef(result)
			}
		}

		if (history.backable) {
			back.setImageResource(R.drawable.back)
		} else {
			back.setImageResource(R.drawable.back_no)
		}
		if (history.forwardable) {
			forward.setImageResource(R.drawable.forward)
		} else {
			forward.setImageResource(R.drawable.forward_no)
		}
//		back.setImageResource(R.drawable.back_no)
//		forward.setImageResource(R.drawable.forward_no)

		val suggestions = new SearchRecentSuggestions(this,
				"lojban dictionary", 1);

		lojen setOnClickListener new OnClickListener() {
			def onClick(v: View) {
				Log.d("LojbanDictionary", "clicked lojen button")
				val result = dic lojToEn
					input.getText.toString.trim.toLowerCase
				if (result._1 != "") {
					history.add(false, result._1)
					if (history.backable)
						back.setImageResource(R.drawable.back)
					suggestions.saveRecentQuery(result._1, null);
				}
				Log.d("LojbanDictionary", "putDef(result) will do")
				putDef(result)
				Log.d("LojbanDictionary", "putDef(result) done")
			}
		}

		enloj setOnClickListener new OnClickListener() {
			def onClick(v: View) = {
				val en = input.getText.toString.trim.toLowerCase
				val list = dic enToLoj en
				if (list != Nil && list(0)._1 != "") {
					history.add(true, en)
					if (history.backable)
						back.setImageResource(R.drawable.back)
					suggestions.saveRecentQuery(en, null);
				}
				mkEnLoj(list)}
			}

		rafsi setOnClickListener new OnClickListener() {
			def onClick(v: View) {
				val r = input.getText.toString.trim
				val result = dic rafsi r
				if (result._1 != "") {
					history.add(false, result._1)
					if (history.backable)
						back.setImageResource(R.drawable.back)
					suggestions.saveRecentQuery(r, null);
				}
				putDef(result)}}
	}

	override def onResume {
		super.onResume
		if (sp.contains("lang")) {
			lojen.setText("jbo -> " + sp.getString("lang", ""))
			enloj.setText(sp.getString("lang", "") + " -> jbo")
			resetCompletion
		}
		if (sp.contains("orientation")) {
			sp.getString("orientation", "auto") match {
				case "auto" => setRequestedOrientation(
					ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
				case "portrait" => setRequestedOrientation(
					ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
				case "landscape" => setRequestedOrientation(
					ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
			}
		}
	}

	override def onPause{
		spre.putString("history", Tools showBoolStrings(history))
		spre.commit
		super.onPause
	}

	override def onCreateOptionsMenu(menu: Menu): Boolean = {
		menu.add(Menu.NONE, 2, 2, "lo se cuxna")
		if (spr.getBoolean("sarji", false) == false) {
			menu.add(Menu.NONE, 1, 1, "ko sarji")
		}
		menu.add(Menu.NONE, 0, 0, "vimcu lo vreji")
		return super.onCreateOptionsMenu(menu)
	}

	override def onOptionsItemSelected(item: MenuItem): Boolean = {
		item.getItemId() match {
		case 2 =>
			val intent = new Intent(this, classOf[Preference])
			startActivity(intent)
		case 1 =>
			val skuList = new ArrayList[String]();
			skuList.add("sarji")
			val querySkus = new Bundle()
			querySkus.putStringArrayList("ITEM_ID_LIST", skuList)
			val skuDetails = mService.getSkuDetails(
				3, getPackageName(), "inapp", querySkus)
			val response = skuDetails.getInt("RESPONSE_CODE")
			if (response == 0) {
				val responseList: ArrayList[String]
					= skuDetails.getStringArrayList(
						"DETAILS_LIST")
				for (thisResponse <- responseList) {
					val obj = new JSONObject(thisResponse)
					val sku = obj.getString("productId")
					val price = obj.getString("price")
					Toast.makeText(this, sku,
						Toast.LENGTH_SHORT).show()
					Toast.makeText(this, price,
						Toast.LENGTH_SHORT).show()
					val buyIntentBundle = mService.
						getBuyIntent(3, getPackageName(),
							sku, "inapp", "hoge")
					val pendingIntent: PendingIntent =
						buyIntentBundle.
							getParcelable("BUY_INTENT")
					startIntentSenderForResult(
						pendingIntent.getIntentSender(),
						1001, new Intent(),
						Integer.valueOf(0),
						Integer.valueOf(0),
						Integer.valueOf(0))
				}
			}
			spre.putBoolean("sarji", true)
		case 0 =>
			Log.d("LojbanDictionary", "history.clear will do")
			history.clear
			Log.d("LojbanDictionary", "history.clear done")
			putDef(("", "coi rodo mi jbovlaste", List()))
			back.setImageResource(R.drawable.back_no)
			forward.setImageResource(R.drawable.forward_no)
//			Log.d("LojbanDictionary", "putDef done")
		}
		return true
	}

	def putDef(result: (String, String, List[String])) {
		valsi.setText(result._1)
		velcki.setText(Html.fromHtml(result._2))
		field.removeAllViews
		field.addView(valsi)
		field.addView(velcki)
		mkLinks(result._3)
	}

	def mkLinks(list: List[String]) {
		for (v <- list) {
			val tv = new TextView(this)
			tv.setTextSize(30)
			tv.setText(v)
			tv.setClickable(true)
			tv.setOnClickListener(new View.OnClickListener() {
				def onClick(_v: View) {
					val result = dic.lojToEn(v)
					if (result._1 != "") {
						history.add(false, result._1)
						if (history.backable)
							back.setImageResource(R.drawable.back)
					}
					putDef(result)
				}
			})
			field.addView(tv)
		}
	}

	def mkEnLoj(list: List[(String, String)]) {
		field.removeAllViews
		for (result <- list) {
			val tv1 = new TextView(this)
			val tv2 = new TextView(this)
			tv1.setTextSize(30)
			tv1.setText(result._1)
			tv1.setOnClickListener(new View.OnClickListener() {
				def onClick(v: View) {
					val result2 = dic.lojToEn(result._1)
					if (result._1 != "") {
						history.add(false, result._1)
						if (history.backable)
							back.setImageResource(R.drawable.back)
					}
					putDef(result2)
				}
			})
			tv1.setClickable(true)
			tv2.setText(Html.fromHtml(result._2))
			field.addView(tv1)
			field.addView(tv2)
		}
	}

	def back(view: View) = backGen

	def backGen(): Boolean = {
		val (ret, move) = history.backward
		var (en, str) = history.get
		if (move) {
			forward.setImageResource(R.drawable.forward)
			if (en) mkEnLoj(dic enToLoj str)
			else putDef(dic lojToEn str)
			if (!ret) back.setImageResource(R.drawable.back_no)
		}
		return ret
	}

	def forward(view: View) {
		val (ret, move) = history.forward
		var (en, str) = history.get
		if (move) {
			back.setImageResource(R.drawable.back)
			if (en) mkEnLoj(dic enToLoj str)
			else putDef(dic lojToEn str)
			if (!ret) forward.setImageResource(R.drawable.forward_no)
		}
	}

	override def onKeyDown(keyCode: Int, event: KeyEvent): Boolean = {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (sp.contains("backbutton") &&
				sp.getBoolean("backbutton", false)) {
				if (history.backable) {
					backGen
					return true
				} else {
					return super.onKeyDown(keyCode, event)
				}
			} else {
				return super.onKeyDown(keyCode, event)
			}
		}
		return super.onKeyDown(keyCode, event)
	}
}
