package com.tander.inventmd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBWorker extends SQLiteOpenHelper {
	private final Context myContext;

	// TABLE/article/ INFORMATTION
	public static final String TABLE_ARTICLE = "article";
	public static final String ARTICLE_ID = "_id";
	public static final String ARTICLE_BARCODE = "barcode";
	public static final String ARTICLE_ARTNAME = "arname";
	public static final String ARTICLE_ARCODE = "arcode";
	public static final String ARTICLE_ARQUANT = "arquant";
	public static final String ARTICLE_REQUANT = "requant";

	// TABLE/art_recalc/ INFORMATTION
	public static final String TABLE_ARTREC = "art_recalc";
	public static final String ARTR_ID = "_id";
	public static final String ARTR_ARCODE = "arcode";
	public static final String ARTR_REQUANT = "requant";

	// DATABASE INFORMATION
	private static String DB_PATH; // = "/data/data/com.tander.inventmd/files/";
	static final String DB_NAME = "invmd.s3db";
	static final int DB_VERSION = 1;

	public DBWorker(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.myContext = context;
		DB_PATH = myContext.getFilesDir().getPath() + '/';		
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		//

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		//
	}

	private boolean checkDataBase(Boolean updating) throws IOException {
		boolean result;
		File dbFile = new File(DB_PATH + DB_NAME);

		if (updating)
			dbFile.delete();
		if (dbFile.exists()) {
			result = true;
		} else {
			dbFile.createNewFile();
			result = false;
		}
		return result;

	}

	public void createDataBase(Boolean updating) throws IOException {

		if (checkDataBase(updating)) {

			// if (dbExist != true) {
			// вызывая этот метод создаем пустую базу, позже она будет
			// перезаписана
			// File dbFile = new File(DB_PATH + DB_NAME);

			this.getWritableDatabase();
		} else {
			this.getWritableDatabase();

			try {
				copyDataBase();
			} catch (IOException e) {
				throw new Error("Error copying database" + e.getMessage());
			}
		}

	}

	private void copyDataBase() throws IOException {

		// Открываем локальную БД как входящий поток
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Путь ко вновь созданной БД
		String outFileName = DB_PATH + DB_NAME;

		// Открываем пустую базу данных как исходящий поток
		OutputStream myOutput = new FileOutputStream(outFileName);

		// перемещаем байты из входящего файла в исходящий
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}

		// закрываем потоки
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	public SQLiteDatabase openDataBase() throws SQLException {
		// * открываем БД
		String myPath = DB_PATH + DB_NAME;
		SQLiteDatabase myDataBase = SQLiteDatabase.openDatabase(myPath, null,
				SQLiteDatabase.OPEN_READWRITE);
		return myDataBase;
	}

}
