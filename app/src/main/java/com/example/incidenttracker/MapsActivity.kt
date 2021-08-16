package com.example.incidenttracker

import android.content.ContentValues.TAG
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Address
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.incidenttracker.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*

class MapsActivity : AppCompatActivity() {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    lateinit var db: FirebaseFirestore
    val incidentList = mutableListOf<IncidentReport>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment


        db = FirebaseFirestore.getInstance()
        val docRef = db.collection("reports")
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val incident = document.toObjects(IncidentReport::class.java)
                    incidentList.addAll(incident)
                    mapFragment.getMapAsync(object : OnMapReadyCallback {
                        override fun onMapReady(p0: GoogleMap) {
                            mMap = p0
                            Log.d(TAG, "map: ${incidentList}")

                            for (incident in incidentList) {
                                val location = LatLng(incident.incidentLat, incident.incidentLng)
                                val locName = incident.incidentLocation
                                val bitmapDescriptor : BitmapDescriptor? = when (incident.incidentType){
                                    "Accident" -> bitmapDescriptorFromVector(this@MapsActivity, R.drawable.ic_accident_24)
                                    "Road Block" -> bitmapDescriptorFromVector(this@MapsActivity, R.drawable.ic_road_block)
                                    "Crime/Theft" -> bitmapDescriptorFromVector(this@MapsActivity, R.drawable.ic_crime)
                                    "Road Construction" -> bitmapDescriptorFromVector(this@MapsActivity, R.drawable.ic_road_construction)
                                    "Fire" -> bitmapDescriptorFromVector(this@MapsActivity, R.drawable.ic_fire)
                                    else -> bitmapDescriptorFromVector(this@MapsActivity, R.drawable.ic_other)
                                }

                                mMap.addMarker(MarkerOptions().position(location).title(locName))?.setIcon(bitmapDescriptor)
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(location))
                            }

                            mMap.setOnMapLongClickListener {
                                val lat: Double = it.latitude
                                val lng: Double = it.longitude

                                val context :Context = applicationContext
                                val geocoder = Geocoder(context, Locale.getDefault())
                                val addresses: List<Address> = geocoder.getFromLocation(lat, lng, 1)
                                val loc = addresses[0].locality
                                Log.d(TAG, "location: ${loc}")
                                Toast.makeText(
                                    this@MapsActivity,
                                    "opened dialog",
                                    Toast.LENGTH_LONG
                                ).show()
                                IncidentDialogFragment.newInstance(lat, lng, loc)
                                    .show(supportFragmentManager, "")
                            }

//                            mMap.setOnMarkerClickListener {
//
//                            }
                        }
                    })

                    Log.d(TAG, "DocumentSnapshot data: ${incidentList}")
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

        }
    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }


}
