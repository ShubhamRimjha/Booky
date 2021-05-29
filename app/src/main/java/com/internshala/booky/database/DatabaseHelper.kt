package com.internshala.booky.database

import android.app.Activity
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DatabaseHelper : SQLiteOpenHelper(Activity() as Context, "database", null, 0) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE tableFav(" +
                    "bookId TEXT PRIMARY KEY," +
                    "bookName TEXT," +
                    "bookAuthor TEXT," +
                    "bookCost TEXT," +
                    "bookRating TEXT," +
                    "bookDesc TEXT," +
                    "bookImg TEXT" +
                    ")"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        TODO("Not yet implemented")
    }
}

