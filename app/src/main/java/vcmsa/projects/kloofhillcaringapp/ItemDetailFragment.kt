package vcmsa.projects.kloofhillcaringapp

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore

class ItemDetailFragment : Fragment() {

    private lateinit var detailImage: ImageView
    private lateinit var detailName: TextView
    private lateinit var detailDesc: TextView
    private lateinit var detailPrice: TextView
    private lateinit var detailAvailability: TextView
    private lateinit var detailGuide: TextView
    private lateinit var detailDeposit: TextView
    private lateinit var guideBtn: MaterialButton
    private lateinit var pageTitle: TextView

    private val firestore = FirebaseFirestore.getInstance()
    private var equipmentId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_item_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detailImage = view.findViewById(R.id.detailImage)
        detailName = view.findViewById(R.id.detailName)
        detailDesc = view.findViewById(R.id.detailDesc)
        detailPrice = view.findViewById(R.id.detailPrice)
        detailGuide = view.findViewById(R.id.detailGuide)
        detailDeposit = view.findViewById(R.id.detailDeposit)
        guideBtn = view.findViewById(R.id.guideBtn)
        detailAvailability = view.findViewById(R.id.detailAvailability)
        pageTitle = view.findViewById(R.id.pageTitle)

        val returnToCatalogueBtn: MaterialButton = view.findViewById(R.id.returnToCatalogueBtn)

        pageTitle.visibility = View.GONE
        detailGuide.visibility = View.GONE
        detailDeposit.visibility = View.GONE // hidden until we know there’s a deposit

        equipmentId = arguments?.getString("equipmentId")
        if (!equipmentId.isNullOrEmpty()) {
            loadEquipmentDetails(equipmentId!!)
        } else {
            Toast.makeText(requireContext(), "No equipment ID provided", Toast.LENGTH_SHORT).show()
        }

        guideBtn.setOnClickListener {
            val showing = detailGuide.visibility == View.GONE
            detailGuide.visibility = if (showing) View.VISIBLE else View.GONE
            pageTitle.visibility = if (showing) View.VISIBLE else View.GONE
            guideBtn.text = if (showing) "Hide Guide" else "Show Guide"
        }

        // Logic for Return to Catalogue button
        returnToCatalogueBtn.setOnClickListener {
            // Replace this fragment with your catalogue fragment
            val fragmentManager = parentFragmentManager
            val catalogueFragment = CatalogFragment() // Replace with your actual catalogue fragment
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, catalogueFragment)
                .addToBackStack(null)
                .commit()
        }

    }

    private fun loadEquipmentDetails(id: String) {
        firestore.collection("equipment").document(id).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val equipment = doc.toObject(Item::class.java)
                    if (equipment != null) {
                        detailName.text = equipment.name
                        detailDesc.text = equipment.description
                        // Format price with 2 decimal places
                        detailPrice.text = "Price: R${String.format("%.2f", equipment.price)} per month"

                        // Set availability text and color using hex codes
                        if (equipment.available) {
                            detailAvailability.text = "Available"
                            detailAvailability.setTextColor(Color.parseColor("#4CAF50")) // green
                        } else {
                            detailAvailability.text = "Not Available"
                            detailAvailability.setTextColor(Color.parseColor("#F44336")) // red
                        }


                        // Show deposit if set, formatted with 2 decimals
                        if (equipment.hasDeposit && equipment.depositAmount != null) {
                            detailDeposit.visibility = View.VISIBLE
                            detailDeposit.text =
                                "Deposit Required: R${String.format("%.2f", equipment.depositAmount)}"
                        } else {
                            detailDeposit.visibility = View.GONE
                        }

                        val bitmap = base64ToBitmap(equipment.imageBase64)
                        detailImage.setImageBitmap(bitmap ?: return@addOnSuccessListener)
                        detailGuide.text = equipment.guide ?: "No guide available for this item"
                    }
                } else {
                    Toast.makeText(requireContext(), "Equipment not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to load: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun base64ToBitmap(base64: String?): Bitmap? {
        return try {
            if (base64.isNullOrEmpty()) return null
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (_: Exception) {
            null
        }
    }

    companion object {
        fun newInstance(equipmentId: String): ItemDetailFragment {
            val fragment = ItemDetailFragment()
            val args = Bundle()
            args.putString("equipmentId", equipmentId)
            fragment.arguments = args
            return fragment
        }
    }
}
