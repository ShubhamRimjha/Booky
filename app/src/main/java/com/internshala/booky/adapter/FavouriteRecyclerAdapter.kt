package com.internshala.booky.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.internshala.booky.R
import com.internshala.booky.activity.DescriptionActivity
import com.internshala.booky.database.BookEntity
import com.squareup.picasso.Picasso

class FavouriteRecyclerAdapter(
    val context: Context, val bookList: List<BookEntity>
) :
    RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>() {


    class FavouriteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTileTitle: TextView = view.findViewById(R.id.txtBookTileTitle)
        val txtTileAuthor: TextView = view.findViewById(R.id.txtBookTileAuthor)
        val txtTileCost: TextView = view.findViewById(R.id.txtBookTilePrice)
        val txtTileRating: TextView = view.findViewById(R.id.txtDashCardRating)
        val imgCover: ImageView = view.findViewById(R.id.imgBookTileImage)
        val linearLayout: LinearLayout = view.findViewById(R.id.LlFavCard)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_favorite_single_row, parent, false)

        return FavouriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavouriteViewHolder, position: Int) {
        val itemObj = bookList[position]

        holder.txtTileTitle.text = itemObj.bookName
        holder.txtTileAuthor.text = itemObj.bookAuthor
        holder.txtTileCost.text = itemObj.bookPrice
        holder.txtTileRating.text = itemObj.bookRating
//        holder.imgCover.setImageResource(itemObj.bookImage)
        Picasso.get().load(itemObj.bookImage).placeholder(R.drawable.ic_book)
            .error(R.drawable.ic_error).into(holder.imgCover)

        holder.linearLayout.setOnClickListener {
            context.startActivity(
                Intent(
                    context,
                    DescriptionActivity::class.java
                ).putExtra("bookID", itemObj.book_id)
            )
        }

    }

    override fun getItemCount(): Int {
        return bookList.size
    }
}
