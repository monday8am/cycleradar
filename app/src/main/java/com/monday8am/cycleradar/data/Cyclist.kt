package com.monday8am.cycleradar.data


data class Cyclist(val longitude: Double,
                   val latitude: Double,
                   val id: String) {

    fun toHash(): HashMap<String, Any> {
        val hash = hashMapOf<String, Any>()

        hash["id"] = id
        hash["longitude"] = longitude
        hash["latitude"] = latitude
        return hash
    }

    val location: UserLocation
        get() = UserLocation(this.longitude, this.latitude)
}
