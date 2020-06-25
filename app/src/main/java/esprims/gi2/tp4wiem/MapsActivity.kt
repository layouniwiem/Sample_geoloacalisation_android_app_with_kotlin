package esprims.gi2.tp4wiem

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationProviderClient= FusedLocationProviderClient(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType=GoogleMap.MAP_TYPE_SATELLITE
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        val Monastir=LatLng(35.76, 10.81)
        val Sousse= LatLng (35.82, 10.64)
        val Tunis=LatLng (36.8, 10.17)
        mMap.addCircle(CircleOptions()
            .center(Tunis)
            .radius(30000.0)
            .strokeColor(Color.BLUE))
       // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Monastir,10.0f))
       // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10.0f))
        //mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.addMarker(MarkerOptions().position(Monastir).title("Monastir"))
        mMap.addMarker(MarkerOptions().position(Sousse).title("Sousse"))
        mMap.addPolyline(PolylineOptions().add(Monastir,Sousse))
        fun placeMarkerOnMap(location: LatLng) {
            // 1
            val markerOptions = MarkerOptions().position(location)
            // 2
            mMap.addMarker(markerOptions)
        }
        // mMap.animateCamera(CameraUpdateFactory.newLatLng(Monastir))
        mMap.setOnMapClickListener{
            Toast.makeText(applicationContext, ""+it.latitude.toString()+" "+it.longitude.toString(), Toast.LENGTH_LONG).show()
        }
        mMap.setOnMarkerClickListener {
            intent= Intent(Intent.ACTION_VIEW, Uri.parse("http://fr.wikipedia.org/wiki/"+it.title.toString()))
            startActivity(intent)
            true
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                applicationContext,
                "vous devez activer l'autorisation",
                Toast.LENGTH_SHORT
            ).show()
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else{
            fusedLocationProviderClient.lastLocation.addOnSuccessListener(this) {
                    location ->
                // Got last known location. In some rare situations this can be null.
                if (location != null) {
                    var lastLocation = location
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    placeMarkerOnMap(currentLatLng)
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
                    val geocoder = Geocoder(applicationContext, Locale.getDefault())
                    val address = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    Toast.makeText(this, address.get(0).getAddressLine(0).toString(), Toast.LENGTH_LONG).show()
                }else
                    Toast.makeText(this, "Unknown last location", Toast.LENGTH_LONG).show()
            }
        }
        val cameraPosition = CameraPosition.builder()
        .target(Monastir)
        .zoom(10f)
        .bearing(45f)
        .tilt(90f)
        .build()
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                onMapReady(mMap)
            else
                Toast.makeText(applicationContext, "acces no autorisé", Toast.LENGTH_SHORT).show()

        }

    }

}