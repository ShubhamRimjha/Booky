package com.internshala.booky.model

data class ApiList(
    val url_get_book_desc: String = "http://13.235.250.119/v1/book/get_book/",
    val url_get_book_list: String = "http://13.235.250.119/v1/book/fetch_books/"
, val url_get_restaurant_list: String = "http://13.235.250.119/v2/restaurants/fetch_result/"
)
