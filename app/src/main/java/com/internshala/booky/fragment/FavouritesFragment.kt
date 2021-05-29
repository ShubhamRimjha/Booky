package com.internshala.booky.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.internshala.booky.R
import com.internshala.booky.activity.TAG
import com.internshala.booky.adapter.FavouriteRecyclerAdapter
import com.internshala.booky.database.BookDatabase
import com.internshala.booky.database.BookEntity

lateinit var progressLayout: RelativeLayout
lateinit var progressBar: ProgressBar
var bookFavList = listOf<BookEntity>()

class FavouritesFragment : Fragment() {

    lateinit var recyclerFavourite: RecyclerView

    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var recyclerAdapter: FavouriteRecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_favourites, container, false)
        recyclerFavourite = view.findViewById(R.id.recyclerFavourite)

        layoutManager = GridLayoutManager(activity, 2)

        progressLayout = view.findViewById(R.id.progressLayout)

        progressBar = view.findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE

        val async = DBFavAsyncTask(context as Context).execute()
        val result = async.get()

//------------------------need to get the list of books from here------------------------//
        if (result) {
            recyclerAdapter = FavouriteRecyclerAdapter(activity as Context, bookFavList)

            recyclerFavourite.adapter = recyclerAdapter

            recyclerFavourite.layoutManager = layoutManager

        } else {
            Toast.makeText(
                context,
                "No favourites added",
                Toast.LENGTH_SHORT
            ).show()
        }
        return view
    }

    class DBFavAsyncTask(context: Context) :
        AsyncTask<Void, Void, Boolean>() {

        /*
        -> Retrieve list of all books from DB.
         */

        val db = Room.databaseBuilder(context, BookDatabase::class.java, "book_db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            val bookList: List<BookEntity> = db.bookDao().getAllBooks()
            Log.i(TAG, "doInBackground: retrieved")
            if (bookList.isEmpty()) return false

            progressLayout.visibility = View.GONE
            bookFavList = bookList
            return true

        }
    }


}