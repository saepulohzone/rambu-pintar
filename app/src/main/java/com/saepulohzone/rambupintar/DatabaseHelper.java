package com.saepulohzone.rambupintar;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteAssetHelper
{
	private static String TAG="DatabaseHelper";
	private static final String	DB_NAME	= "rambu_pintar";
	private static final int	DB_VER	= 1;

	private static final String	TB_DATA	= "rambu";
	public static final String	ID		= "id";
	public static final String	ARTI	= "arti_rambu";
	public static final String	KATEGORI= "kategori";
	public static final String	PATH	= "path";

	private static DatabaseHelper	dbInstance;
	private static SQLiteDatabase	db;
	Cursor mCursor;

	private DatabaseHelper(Context context)	{
		super(context, DB_NAME, null, DB_VER);
		Log.d(TAG, "ini DatabaseHelper");
	}

	public static DatabaseHelper getInstance(Context context) {
		Log.d(TAG, "Pemerikasaan database");
		if (dbInstance == null) {
			dbInstance = new DatabaseHelper(context);
			db = dbInstance.getWritableDatabase();
			Log.d(TAG, "Pemerikasaan database Berhasil");
		}else{Log.d(TAG, "Pemerikasaan database Gagal");}
		Log.d(TAG, "Kembali kan database");
		return dbInstance;
	}

	@Override
	public synchronized void close() {
		Log.d(TAG, "Menutup database");
		super.close();
		if (dbInstance != null) {
			dbInstance.close();
			Log.d(TAG, "Menutup database berhasil");
		}
	}

	public ArrayList<String> getAllRambu()
	{
		ArrayList<String> lisKamus = new ArrayList<String>();

		Cursor cursor = db.query(TB_DATA, new String[] { ID, ARTI,
				KATEGORI, PATH }, null, null, null, null,ID);
		if (cursor.getCount() >= 1)
		{
			cursor.moveToFirst();

			do
			{
				lisKamus.add(cursor.getString(3));

			} while (cursor.moveToNext());
		}
		return lisKamus;
	}

	public Cursor getAll(){
		Log.d(TAG, "Memerikas isi tabel");
		mCursor=db.query(TB_DATA, new String[] { "_id","_id", "istilah",
				"arti"}, null, null, null, null, "istilah");
		Log.d(TAG, "isi database disimpan di cursor");
		return mCursor;
	}

	public Cursor getRambuByCategory(String query) {
		Log.d(TAG, "memasukan data ke cursor");
			mCursor = db.query(TB_DATA,
					new String[]{
							ID, ARTI,
							KATEGORI,
							PATH}, KATEGORI +
							"='" + query + "'",
					null, null, null, ID);
			Log.d(TAG, "isi database disimpan di cursor");
		return mCursor;
	}

	public Cursor getRambuSearch(String query) {
		Log.d(TAG, "memasukan data ke cursor");
			mCursor = db.query(TB_DATA,
					new String[]{
							ID, ARTI,
							KATEGORI,
							PATH}, ARTI + " like '%"
							+ query + "%'",
					null, null, null, ARTI);
			Log.d(TAG, "isi database disimpan di cursor");
		return mCursor;
	}
}
