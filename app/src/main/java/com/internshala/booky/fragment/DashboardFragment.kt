package com.internshala.booky.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.internshala.booky.R
import com.internshala.booky.adapter.DashboardRecyclerAdapter
import com.internshala.booky.model.ApiList
import com.internshala.booky.model.Book
import com.internshala.booky.model.Token
import com.internshala.booky.util.ConnectionManager
import org.json.JSONException
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class DashboardFragment : Fragment() {

    lateinit var recyclerDashboard: RecyclerView

    var flag: Boolean? = null
    lateinit var layoutManager: RecyclerView.LayoutManager

    lateinit var recyclerAdapter: DashboardRecyclerAdapter

    lateinit var progressLayout: RelativeLayout

    lateinit var progressBar: ProgressBar

    var ratingComparator = Comparator<Book> { book1, book2 ->
        if (book1.bookRating.compareTo(
                book2.bookRating,
                true
            ) == 0
        ) book1.bookName.compareTo(book2.bookName, true)
        else
            book1.bookRating.compareTo(book2.bookRating, true)
    }

    var priceComparator = Comparator<Book> { book1, book2 ->
        if (book1.bookPrice.compareTo(
                book2.bookPrice,
                true
            ) == 0
        ) book1.bookName.compareTo(book2.bookName, true)
        else
            book1.bookPrice.compareTo(book2.bookPrice, true)
    }

    var bookInfoList = arrayListOf<Book>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        setHasOptionsMenu(true)

        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)

        layoutManager = LinearLayoutManager(activity)

        recyclerAdapter = DashboardRecyclerAdapter(activity as Context, bookInfoList)

        progressLayout = view.findViewById(R.id.progressLayout)

        progressBar = view.findViewById(R.id.progressBar)

        progressLayout.visibility = View.VISIBLE


        val queue = Volley.newRequestQueue(activity as Context)
        val url = ApiList().url_get_book_list
        if (ConnectionManager().checkConnectivity(activity as Context)) {

            val jsonObjectRequest = object : JsonObjectRequest(url, null, Response.Listener {
                try {
                    progressLayout.visibility = View.GONE
                    //  handle the response here
                    val success = it.getBoolean("success")

                    if (success) {
                        val data = it.getJSONArray("data")

                        for (i in 0 until data.length()) {

                            val bookJsonObject = data.getJSONObject(i)

                            val bookObject = Book(
                                bookJsonObject.getString("book_id"),
                                bookJsonObject.getString("name"),
                                bookJsonObject.getString("author"),
                                bookJsonObject.getString("rating"),
                                bookJsonObject.getString("price"),
                                bookJsonObject.getString("image")
                            )
                            bookInfoList.add(bookObject)
                        }
                        recyclerDashboard.layoutManager = layoutManager

                        recyclerDashboard.adapter = recyclerAdapter

                    } else {
                        Toast.makeText(
                            activity as Context,
                            "Response from server null",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(
                        activity as Context,
                        "Unknown exception occured",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                }

            }, Response.ErrorListener {
                //  handle the errors here
                println("Error is $it")
                Toast.makeText(activity as Context, "Volley Error Occured", Toast.LENGTH_SHORT)
                    .show()
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
            AlertDialog.Builder(activity as Context).setTitle("Success")
                .setMessage("Internet Not Available").setPositiveButton("Settings") { _, _ ->
                    val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingIntent)
                    activity?.finish()
                }.setNegativeButton("Exit") { _, _ ->
                    ActivityCompat.finishAffinity(activity as Activity)

                }.create().show()
        }
        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_dashboard, menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {

            R.id.action_sort_rating -> {
                Collections.sort(bookInfoList, ratingComparator)
                bookInfoList.reverse()
                flag = null
            }
            R.id.action_sort_price -> {

                Collections.sort(bookInfoList, priceComparator)
                if (flag == true) {
                    bookInfoList.reverse()
                    flag = false
                } else flag = true
            }
        }
        recyclerAdapter.notifyDataSetChanged()
        return true
    }


}