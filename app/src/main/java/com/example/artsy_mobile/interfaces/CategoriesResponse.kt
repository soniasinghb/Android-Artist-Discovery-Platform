package com.example.artsy_mobile.interfaces

data class CategoriesResponse (
    val result: Boolean,
    val message: List<Categories>
)

data class Categories(
    val gene_name: String,
    val gene_img: String,
    val gene_description: String
)