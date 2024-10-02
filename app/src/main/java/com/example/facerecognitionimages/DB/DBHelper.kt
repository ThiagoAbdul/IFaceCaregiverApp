package com.example.facerecognitionimages.DB

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.facerecognitionimages.face_recognition.FaceClassifier.Recognition
import com.example.facerecognitionimages.utils.parseEmbeddingToString

class DBHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        // TODO Auto-generated method stub
        db.execSQL(
            "create table faces " +
                    "(id integer primary key, name text,embedding text)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS faces")
        onCreate(db)
    }

    fun insertFace(name: String?, embedding: Any): Boolean {
        val floatList = embedding as Array<FloatArray>
        val embeddingString = parseEmbeddingToString(floatList)

        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(FACE_COLUMN_NAME, name)
        contentValues.put(FACE_COLUMN_EMBEDDING, embeddingString)
        db.insert("faces", null, contentValues)
        return true
    }

    fun getData(id: Int): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("select * from faces where id=$id", null)
    }

    fun numberOfRows(): Int {
        val db = this.readableDatabase
        return DatabaseUtils.queryNumEntries(db, FACE_TABLE_NAME).toInt()
    }

    fun updateFace(id: Int?, name: String?, embedding: String?): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(FACE_COLUMN_NAME, name)
        contentValues.put(FACE_COLUMN_EMBEDDING, embedding)
        db.update(
            FACE_TABLE_NAME, contentValues, "id = ? ", arrayOf(
                Integer.toString(
                    id!!
                )
            )
        )
        return true
    }

    fun deleteFace(id: Int?): Int {
        val db = this.writableDatabase
        return db.delete(
            FACE_TABLE_NAME,
            "id = ? ", arrayOf(Integer.toString(id!!))
        )
    }

    @get:SuppressLint("Range")
    val allFaces: HashMap<String?, Recognition>
        get() {
            //hp = new HashMap();
            val db = this.readableDatabase
            val res = db.rawQuery("select * from faces", null)
            res.moveToFirst()
            val registered = HashMap<String?, Recognition>()
            while (!res.isAfterLast) {
                val embeddingString = res.getString(res.getColumnIndex(FACE_COLUMN_EMBEDDING))
                val stringList = embeddingString.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                    .toTypedArray()
                val embeddingFloat = ArrayList<Float>()
                for (s in stringList) {
                    embeddingFloat.add(s.toFloat())
                }
                val bigArray = Array(1) {
                    FloatArray(
                        1
                    )
                }
                val floatArray = FloatArray(embeddingFloat.size)
                for (i in embeddingFloat.indices) {
                    floatArray[i] = embeddingFloat[i]
                }
                bigArray[0] = floatArray
                // embeddingFloat.remove(embeddingFloat.size()-1);
                val recognition =
                    Recognition(res.getString(res.getColumnIndex(FACE_COLUMN_NAME)), bigArray)
                registered.putIfAbsent(recognition.title, recognition)
                res.moveToNext()
            }
            return registered
        }

    companion object {
        const val DATABASE_NAME = "MyFaces.db"
        const val FACE_TABLE_NAME = "faces"
        const val FACE_COLUMN_ID = "id"
        const val FACE_COLUMN_NAME = "name"
        const val FACE_COLUMN_EMBEDDING = "embedding"
    }
}