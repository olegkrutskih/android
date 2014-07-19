package com.tander.inventmd.slidelist;

import java.util.ArrayList;
import java.util.HashMap;

import com.tander.inventmd.R;
import com.tander.inventmd.SQLController;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class RC_Fragment extends Fragment {

	EditText etBarCode;
	TextView textView1;
	String tvArcode = "";
	String tvArname = "";
	String tvArquant = "";
	String etRequant = "";
	ListView lvScannedArt;

	ArrayList<HashMap<String, Object>> artArray = new ArrayList<HashMap<String, Object>>();
	String TITLE = "arname"; // Верхний текст
	String DESCRIPTION = "arcode"; // ниже главного
	String QUANTITY = "requant"; // правый текст
	String ID = "artr_id"; // id записи в БД
	// String ICON = "icon"; // будущая картинка`

	// Создаём адаптер ArrayAdapter, чтобы привязать массив к ListView
	// ArrayAdapter<String> artAdapter;
	SimpleAdapter artAdapter;

	SQLController sqlcon;

	ProgressDialog PD;
	private Context myContext;

	public RC_Fragment(Context context) {
		super();
		myContext = context;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater
				.inflate(R.layout.rc_fragment, container, false);

		sqlcon = new SQLController(myContext);

		this.etBarCode = (EditText) rootView.findViewById(R.id.etBarCode);
		this.textView1 = (TextView) rootView.findViewById(R.id.textView1);
		// this.tvArcode = (TextView) rootView.findViewById(R.id.tvArcode);
		// this.tvArname = (TextView) rootView.findViewById(R.id.tvArname);
		// this.tvArquant = (TextView) rootView.findViewById(R.id.tvArquant);
		// this.etRequant = (EditText) rootView.findViewById(R.id.etQuant);

		// tvArcode.setText("");
		// tvArname.setText("");
		// tvArquant.setText("");
		// etRequant.setText("0");
		lvScannedArt = (ListView) rootView.findViewById(R.id.lvScannedArt);

		// Создаём пустой массив для хранения данных об отсканнированном товаре

		// artAdapter = new ArrayAdapter<String>(myContext,
		// R.layout.lv_artlist_item, artArray);
		artAdapter = new SimpleAdapter(myContext, artArray,
				R.layout.lv_artlist_item, new String[] { TITLE, DESCRIPTION,
						QUANTITY, ID }, new int[] { R.id.text1, R.id.text2,
						R.id.tvQuant, R.id.artr_id });
		// Привяжем массив через адаптер к ListView
		lvScannedArt.setAdapter(artAdapter);

		etBarCode.setOnKeyListener(new View.OnKeyListener() {

			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (event.getAction() == KeyEvent.ACTION_UP
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {

					if (etBarCode.getText().toString().equals("")) {
						// Ничего не делаем
					} else {
						textView1.setText(etBarCode.getText().toString());
						if (getArt("barcode", etBarCode.getText().toString(),
								false)) {
							requestQuant(new String[] { tvArcode, tvArname,
									tvArquant, "", "" }, false);
						}
					}
					etBarCode.setText("");
					textView1.setText("Отсканируйте или введите ШК. \n   ");
					// String strCatName = myText.getText.getText().toString();
					return true;
				}
				return false;
			}
		});

		lvScannedArt
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						if (getArt("_id", ((TextView) view
								.findViewById(R.id.artr_id)).getText()
								.toString(), true)) {
							requestQuant(
									new String[] {
											tvArcode,
											tvArname,
											tvArquant,
											((TextView) view
													.findViewById(R.id.tvQuant))
													.getText().toString(),
											((TextView) view
													.findViewById(R.id.artr_id))
													.getText().toString() },
									true);
						}
					}
				});

		// etRequant.setOnKeyListener(new View.OnKeyListener() {
		//
		// @Override
		// public boolean onKey(View v, int keyCode, KeyEvent event) {
		// if (event.getAction() == KeyEvent.ACTION_UP
		// && (keyCode == KeyEvent.KEYCODE_ENTER)) {
		// textView1.setText("Нажата кнопка Enter2!");
		//
		// etBarCode.setText("");
		// etBarCode.requestFocus();
		// // String strCatName = myText.getText.getText().toString();
		// return true;
		// }
		// return false;
		// }
		// });
		updateListArt();
		return rootView;
	}

	@SuppressLint("InflateParams")
	public void requestQuant(String[] info, final boolean rerequest) {
		final String quant = info[4];
		// get prompts.xml view
		LayoutInflater li = LayoutInflater.from(myContext);
		View promptsView = li.inflate(R.layout.prompt_quant, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				myContext);

		// set prompts.xml to alertdialog builder
		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView
				.findViewById(R.id.etQuant);
		TextView textView1 = (TextView) promptsView
				.findViewById(R.id.textView1);

		// set dialog message
		alertDialogBuilder
//				.setCancelable(false)
//				.setNegativeButton("Cancel",
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog, int id) {
//								dialog.cancel();
//
//							}
//						})
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// get user input and set it to result
						// edit text
						// etRequant.setText(userInput.getText());
						afterInputQuant(new String[] { tvArcode,
								userInput.getText().toString(), quant },
								strToDouble(userInput.getText().toString()),
								rerequest);
					}
				});

		textView1.setText("Код: " + info[0] + "\n" + "Товар: " + info[1] + "\n"
				+ "Остаток: " + info[2]);
		// create alert dialog
		final AlertDialog alertDialog = alertDialogBuilder.create();
		InputMethodManager imm = (InputMethodManager) myContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.HIDE_NOT_ALWAYS, 0);
		// show it

		if (rerequest) {
			userInput.setText(info[3]);
			userInput.selectAll();
		}
//		userInput.setOnEditorActionListener(new OnEditorActionListener() {
//
//			@Override
//			public boolean onEditorAction(TextView v, int actionId,
//					KeyEvent event) {
//				if (actionId == EditorInfo.IME_ACTION_NEXT
//						|| actionId == EditorInfo.IME_ACTION_DONE) {
//					alertDialog.getButton(
//							DialogInterface.BUTTON_POSITIVE).callOnClick();
//					return true;
//				}
//				return false;
//			}
//		});

		alertDialog.show();
	}

	private Double strToDouble(String str) {
		Double result;
		try {
			result = Double.parseDouble(str);
		} catch (NumberFormatException e) {
			result = null; // в случае ошибки перевода
		}
		return result;
	}

//	 public void setContext(Context c) {
//	 myContext = c;
//	 }

	private void updateListArt() {

		sqlcon.open(false);
		try {
			Cursor c = sqlcon.readAllArtR();
			if (c != null) {
				c.moveToFirst();
			} else {
				return;
			}
			artArray.clear();
			do {
				HashMap<String, Object> hm;
				hm = new HashMap<String, Object>();
				hm.put(TITLE, c.getString(0)); // Название
				hm.put(DESCRIPTION, c.getString(1)); // Описание
				hm.put(QUANTITY, c.getString(2)); // Посчитано
				hm.put(ID, c.getString(3)); // ID
				// hm.put(ICON, R.drawable.cat_gold); // Картинка
				artArray.add(0, hm);
			} while (c.moveToNext());

			artAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			Toast toast = Toast.makeText(
					this.myContext,
					"Не удалось обновить таблицу посчитанных товаров!"
							+ e.getMessage(), Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
		sqlcon.close();

	}

	private void afterInputQuant(String[] art, Double quant, boolean rerequest) {

		sqlcon.open(false);
		try {

			if (sqlcon.setQuant(quant, art[0], art[2], rerequest)) {

				// HashMap<String, Object> hm;
				// hm = new HashMap<String, Object>();
				// hm.put(TITLE, art[1]); // Название
				// hm.put(DESCRIPTION, art[0]); // Описание
				// hm.put(QUANTITY, art[2]); // Посчитано
				// // hm.put(ICON, R.drawable.cat_gold); // Картинка
				// artArray.add(0, hm);
				// artAdapter.notifyDataSetChanged();

				updateListArt();

				// editText.setText("");

				// ArrayList<HashMap<String, Object>> artList;
				// String TITLE = "arname"; // Верхний текст
				// String DESCRIPTION = "arcode"; // ниже главного
				// // String ICON = "icon"; // будущая картинка`
				//
				// // создаем массив списков
				// artList = new ArrayList<HashMap<String, Object>>();
				//
				//
				//
				//
				// SimpleAdapter adapter = new SimpleAdapter(myContext, artList,
				// R.layout.lv_artlist_item, new String[] { TITLE, DESCRIPTION
				// },
				// new int[] { R.id.text1, R.id.text2 });
				//
				// lvScannedArt.setAdapter(adapter);
			}
		} catch (Exception e) {
			Toast toast = Toast.makeText(this.myContext,
					"Проблема с подключением к БД!" + e.getMessage(),
					Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}
		sqlcon.close();
	}

	private boolean getArt(String key, String val, boolean rerequest) {
		boolean result = false;
		sqlcon.open(false);
		Cursor c;

		if (rerequest) {
			c = sqlcon.readEntryArtR(key, val);
		} else {
			c = sqlcon.readEntryArt(key, val);
		}

		if (c != null) {
			c.moveToFirst();
		} else {
			return false;
		}

		// PD = new ProgressDialog(myContext);
		// PD.setTitle("Please Wait..");
		// PD.setMessage("Loading...");
		// PD.setCancelable(false);
		// PD.show();
		try {
			tvArcode = c.getString(c.getColumnIndex("arcode"));
			tvArname = c.getString(c.getColumnIndex("arname"));
			tvArquant = c.getString(c.getColumnIndex("arquant"));

			// Intent intent = new Intent(this.myContext, ArtDetail.class);

			// startActivity(intent);

			// intent.setContext();

			// Fragment fragment = null;
			// fragment = new ArtDetail();
			// ((ArtDetail) fragment).setContext(this.myContext);
			result = true;
		} catch (Exception e) {
			Toast toast = Toast.makeText(this.myContext,
					"ШК не найден!" + e.getMessage(), Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}

		// PD.dismiss();
		sqlcon.close();
		return result;

	}

}
