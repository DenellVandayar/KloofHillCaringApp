package vcmsa.projects.kloofhillcaringapp

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class LocationFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_location, container, false)

        val mapImage: ImageView = view.findViewById(R.id.mapImage)
        val mapsLink: TextView = view.findViewById(R.id.googleMapsLink)

        val address = "Unit F10 Kingfisher Crest Retirement Village, 18 Knelsby Avenue, Hillcrest"
        val encodedAddress = Uri.encode(address)

        // --- Function to open Google Maps or browser ---
        fun openMap() {
            try {
                val geoIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$encodedAddress"))
                startActivity(geoIntent)
            } catch (e: ActivityNotFoundException) {
                try {
                    val browserIntent = Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/search/?api=1&query=$encodedAddress")
                    )
                    startActivity(browserIntent)
                    Toast.makeText(
                        requireContext(),
                        "No maps app installed — opening in browser.",
                        Toast.LENGTH_SHORT
                    ).show()
                } catch (e: ActivityNotFoundException) {
                    Toast.makeText(requireContext(), "No app found to open map link.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Click listeners for top map and text
        mapImage.setOnClickListener { openMap() }
        mapsLink.setOnClickListener { openMap() }

        // --- OSMDroid Map ---
        val osmMapView: MapView = view.findViewById(R.id.osmMapView)
        Configuration.getInstance().load(
            requireContext(),
            requireContext().getSharedPreferences("osmdroid", 0)
        )
        osmMapView.setMultiTouchControls(true)

        val location = GeoPoint(-29.778480561780054, 30.757661882290552)

        osmMapView.controller.setCenter(location)
        osmMapView.controller.setZoom(18.0)

        // Add marker (no click actions)
        val marker = Marker(osmMapView)
        marker.position = location
        marker.title = "Kingfisher Crest Retirement Village"
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)

        osmMapView.overlays.add(marker)

        return view
    }
}
