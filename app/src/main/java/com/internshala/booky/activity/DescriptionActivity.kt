package com.internshala.booky.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.booky.R
import com.internshala.booky.database.BookDatabase
import com.internshala.booky.database.BookEntity
import com.internshala.booky.model.ApiList
import com.internshala.booky.model.Token
import com.internshala.booky.util.ConnectionManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_description.*
import kotlinx.android.synthetic.main.book_card_row_includable.*
import kotlinx.android.synthetic.main.rating_star_includable.*
import org.json.JSONObject


val TAG = "DB call"
class DescriptionActivity : AppCompatActivity() {

    lateinit var bookName: TextView

    lateinit var bookAuthor: TextView
    lateinit var bookPrice: TextView
    lateinit var bookRating: TextView
    lateinit var bookFullDesc: TextView
    lateinit var bookCover: ImageView
    lateinit var btnAddToFavourites: Button
    lateinit var progressBar: ProgressBar
    lateinit var progressBarLayout: RelativeLayout

    var bookID: String? = "100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        val toolbar: Toolbar = findViewById(R.id.toolbar)

        btnAddToFavourites = findViewById(R.id.btnAddToFavourites)

        progressBar = findViewById(R.id.progressBar)
        progressBarLayout = findViewById(R.id.progressLayout)

        bookName = findViewById(R.id.txtDashCardTitle)
        bookAuthor = findViewById(R.id.txtDashCardAuthor)
        bookPrice = findViewById(R.id.txtDashCardCost)
        bookRating = findViewById(R.id.txtDashCardRating)
        bookCover = findViewById(R.id.imgBookcover)
        bookFullDesc = findViewById(R.id.txtBookFullDesc)


        if (intent != null) bookID = intent.getStringExtra("bookID")
        else
            Toast.makeText(this, "Book not available", Toast.LENGTH_SHORT).show()

        if (bookID == "100") {
            Toast.makeText(this, "Some error", Toast.LENGTH_SHORT).show()
            finish()
        }

        val queue = Volley.newRequestQueue(this@DescriptionActivity)
        val url = ApiList().url_get_book_desc

        if (ConnectionManager().checkConnectivity(this@DescriptionActivity)) {
            val bookIDJson = JSONObject()
            bookIDJson.put("book_id", "$bookID")

            val jsonObjectRequest =
                object : JsonObjectRequest(Method.POST, url, bookIDJson, Response.Listener {
                    val item = it.getJSONObject("book_data")

                    toolbar.title = item.getString("name")
                    txtDashCardTitle.text = item.getString("name")
                    txtDashCardAuthor.text = item.getString("author")
                    txtDashCardCost.text = item.getString("price")
                    txtDashCardRating.text = item.getString("rating")

                    Picasso.get().load(item.getString("image")).placeholder(R.drawable.ic_book)
                        .error(R.drawable.ic_error).into(this.imgBookcover)

                    txtBookFullDesc.text = item.getString("description")

                    progressBarLayout.visibility = View.GONE

                    val bookEntity = BookEntity(
                        bookID?.toInt() as Int,
                        item.getString("name"),
                        item.getString("author"),
                        item.getString("price"),
                        item.getString("rating"),
                        item.getString("description"),
                        item.getString("image")
                    )

                    val checkFav = DBAsyncTask(applicationContext, bookEntity, 1).execute()
                    //this object is to run the async thread
                    val isFav = checkFav.get()

                    Log.i(TAG, "onCreate: $isFav")

                    //  this object runs the methods of async task
                    if (isFav)
                        btnAddToFavourites.text = "Remove from favourites"
                    else
                        btnAddToFavourites.text = "Add to favourites"


                    btnAddToFavourites.setOnClickListener {

                        if (!DBAsyncTask(applicationContext, bookEntity, 1).execute().get()) {
                            val async = DBAsyncTask(applicationContext, bookEntity, 2).execute()
                            val result = async.get()
                            if (result) {
                                Toast.makeText(
                                    this@DescriptionActivity,
                                    "Book added to fav",
                                    Toast.LENGTH_SHORT
                                ).show()
                                btnAddToFavourites.text = "Remove from Favourites"
                            }
                        } else {
                            val async = DBAsyncTask(applicationContext, bookEntity, 3).execute()
                            val result = async.get()
                            if (result) {
                                Toast.makeText(
                                    this@DescriptionActivity,
                                    "Book removed from fav",
                                    Toast.LENGTH_SHORT
                                ).show()
                                btnAddToFavourites.text = "Add to Favourites"
                            }
                        }

                    }
                },
                    {
                        Toast.makeText(
                            this@DescriptionActivity,
                            "Volley Error Occured: $it",
                            Toast.LENGTH_SHORT
                        ).show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["content-type"] = "application/json"
                        headers["token"] = Token().apiToken
                        return headers
                    }


                }
            queue.add(jsonObjectRequest)


        } else {
            AlertDialog.Builder(this@DescriptionActivity).setTitle("Success")
                .setMessage("Internet Not Available").setPositiveButton("Settings") { _, _ ->
                    val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingIntent)
                    finish()
                }.setNegativeButton("Exit") { _, _ ->
                    ActivityCompat.finishAffinity(parent)
                }.create().show()
        }

    }

    class DBAsyncTask(val context: Context, val bookEntity: BookEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {

        /*
        Check mode variable
        Case 1 -> Check DB to see if book is there or not
        Case 2 -> Add book to DB as favorites
        Case 3 -> Remove book from DB
         */

        val db = Room.databaseBuilder(context, BookDatabase::class.java, "book_db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {
                1 -> {
                    val book: BookEntity? = db.bookDao().getBookById(bookEntity.book_id.toString())
                    db.close()
                    return book != null
                }

                2 -> {
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    Log.i(TAG, "doInBackground: inserted")
                    return true
                }

                3 -> {
                    db.bookDao().deleteBook(bookEntity)
                    Log.i(TAG, "doInBackground: removed")
                    db.close()
                    return true
                }
            }
            return false
        }
    }
}


