package com.tander.inventmd;

import java.io.IOException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.view.Gravity;
import android.widget.Toast;

public class SQLController {
	private DBWorker worker;
	private Context myContext;
	private SQLiteDatabase database;

	public SQLController(Context c) {
		myContext = c;
	}

	public SQLController open(boolean updating) throws SQLException {
		worker = new DBWorker(myContext);
		try {
			worker.createDataBase(updating);
		} catch (IOException e) {			
			e.printStackTrace();
		}
		// AssetFileDescriptor f = ourcontext.getAssets().openFd("art.s3db");
		try {

			database = worker.openDataBase();
		} catch (Exception ex) {
			database.close();
		}
		// database = dbhelper.getWritableDatabase();
		return this;

	}

	public void close() {
		worker.close();
	}

	public Cursor readEntryArt(String key, String val) {

		String sql = "select * from article";

		Cursor c = database.rawQuery(sql + " where " + key + "='" + val + "'",
				null);

		if (c != null) {
			c.moveToFirst();
		}
		return c;

	}

	public Cursor readEntryArtR(String key, String val) {

		String sql = "select coalesce((select a.arname from article a where a.arcode = ar.arcode limit 1), 'null') arname, coalesce((select a.arquant from article a where a.arcode = ar.arcode limit 1), 0) arquant, ar.arcode, ar.requant, ar._id from art_recalc ar ";

		Cursor c = database.rawQuery(sql + " where " + key + "='" + val + "'",
				null);

		if (c != null) {
			c.moveToFirst();
		}
		return c;

	}

	public Cursor readAllArt() {
		
		String[] allColumns = new String[] { DBWorker.ARTICLE_ID,
				DBWorker.ARTICLE_BARCODE, DBWorker.ARTICLE_ARTNAME,
				DBWorker.ARTICLE_ARCODE, DBWorker.ARTICLE_ARQUANT,
				DBWorker.ARTICLE_REQUANT };		

		Cursor c = database.query(DBWorker.TABLE_ARTICLE, allColumns, null,
				null, null, null, null);

		if (c != null) {
			c.moveToFirst();
		}
		return c;

	}

	public Cursor readAllArtR() {
		
		String sql = "select coalesce((select a.arname from article a where a.arcode = ar.arcode limit 1), '0') arname, ar.arcode, ar.requant, ar._id from art_recalc ar order by ar.lastdate";

		Cursor c = database.rawQuery(sql, null);

		if (c != null) {
			c.moveToFirst();
		}
		return c;

	}

	public boolean setQuant(Double quant, String arcode, String id,
			boolean rerequest) {
		boolean result = false;

		try {
			ContentValues cv = new ContentValues();
			cv.put(DBWorker.ARTR_REQUANT, quant);
			if (rerequest) {
				database.update(DBWorker.TABLE_ARTREC, cv, DBWorker.ARTR_ID
						+ "=" + id, null);
			} else {
				cv.put(DBWorker.ARTR_ARCODE, arcode);
				database.insert(DBWorker.TABLE_ARTREC, null, cv);
			}

			result = true;
		} catch (Exception e) {
			Toast toast = Toast.makeText(this.myContext,
					"Не удалось добавить запись!\n" + e.getMessage(),
					Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
			result = false;
		}
		return result;
	}

	public Context getContext() {
		return myContext;
	}
}
