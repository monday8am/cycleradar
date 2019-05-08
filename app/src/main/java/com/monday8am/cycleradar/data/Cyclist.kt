package com.monday8am.cycleradar.data


data class Cyclist(val longitude: Double,
                   val latitude: Double,
                   val imageUrl: String? = null,
                   val completed: Boolean = false,
                   val cyclistId: Int = 0)
