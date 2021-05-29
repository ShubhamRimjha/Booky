package com.internshala.booky.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internshala.booky.R
import com.internshala.booky.activity.DescriptionActivity
import com.internshala.booky.model.Book
import com.squareup.picasso.Picasso

class DashboardRecyclerAdapter(
    val context: Context, val data: ArrayList<Book>
) :
    RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {

    class DashboardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDashCardTitle: TextView = view.findViewById(R.id.txtDashCardTitle)
        val txtDashCardAuthor: TextView = view.findViewById(R.id.txtDashCardAuthor)
        val txtDashCardCost: TextView = view.findViewById(R.id.txtDashCardCost)
        val txtDashCardRating: TextView = view.findViewById(R.id.txtDashCardRating)
        val imgCover: ImageView = view.findViewById(R.id.imgBookcover)
        val rlDashCard: RelativeLayout = view.findViewById(R.id.RlDashCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_dashboard_cardview, parent, false)

        return DashboardViewHolder(view)
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        val itemObj = data[position]

        holder.txtDashCardTitle.text = itemObj.bookName
        holder.txtDashCardAuthor.text = itemObj.bookAuthor
        holder.txtDashCardCost.text = itemObj.bookPrice
        holder.txtDashCardRating.text = itemObj.bookRating
//        holder.imgCover.setImageResource(itemObj.bookImage)
        Picasso.get().load(itemObj.bookImage).placeholder(R.drawable.ic_book)
            .error(R.drawable.ic_error).into(holder.imgCover)

        holder.rlDashCard.setOnClickListener {
            context.startActivity(
                Intent(
                    context,
                    DescriptionActivity::class.java
                ).putExtra("bookID", itemObj.bookId)
            )
        }

    }

    override fun getItemCount(): Int {

        return data.size

    }
}