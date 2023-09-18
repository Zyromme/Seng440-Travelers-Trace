package com.enz.ac.uclive.zba29.travelerstrace.model

data class Journey(
    val id: Int,
    val name: String,
    val date: String,
    val latLong: List<List<Double>>, // This will probably change we will have to see
    val totalDistance: Double,
    val image: Int,
    val type: String,
)