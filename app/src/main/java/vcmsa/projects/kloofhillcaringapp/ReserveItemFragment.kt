package vcmsa.projects.kloofhillcaringapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.graphics.Color


class ReserveItemFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var backArrow: ImageButton
    private lateinit var itemImage: ImageView
    private lateinit var itemName: TextView
    private lateinit var itemPrice: TextView
    private lateinit var itemAvailability: TextView
    private lateinit var commentEditText: TextInputEditText
    private lateinit var reserveBtn: MaterialButton

    private var equipmentId: String? = null
    private var equipmentName: String? = null
    private var equipmentPrice: Double? = null
    private var equipmentAvailable: Boolean? = null
    private var equipmentImageUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            equipmentId = it.getString("equipmentId")
        }

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reserve_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Bind views ---
        backArrow = view.findViewById(R.id.backArrow)
        itemImage = view.findViewById(R.id.itemImage)
        itemName = view.findViewById(R.id.itemName)
        itemPrice = view.findViewById(R.id.itemPrice)
        itemAvailability = view.findViewById(R.id.itemAvailability)
        commentEditText = view.findViewById(R.id.commentEditText)
        reserveBtn = view.findViewById(R.id.reserveBtn)

        // --- Back arrow logic ---
        backArrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        loadEquipmentDetails()

        reserveBtn.setOnClickListener {
            saveReservation()
        }
    }

    private fun loadEquipmentDetails() {
        if (equipmentId.isNullOrEmpty()) return

        firestore.collection("equipment").document(equipmentId!!)
            .get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) return@addOnSuccessListener

                equipmentName = doc.getString("name") ?: "No Name"
                equipmentPrice = doc.getDouble("price") ?: 0.0
                equipmentAvailable = doc.getBoolean("available") ?: false
                equipmentImageUrl = doc.getString("imageBase64")

                itemName.text = equipmentName
                itemPrice.text = "R${String.format("%.2f", equipmentPrice)} per month"
                itemAvailability.text = if (equipmentAvailable == true) "Available" else "Not Available"
                itemAvailability.setTextColor(
                    if (equipmentAvailable == true) Color.parseColor("#0DA13C")
                    else Color.parseColor("#FF0000") // red for not available
                )


                if (!equipmentImageUrl.isNullOrEmpty()) {
                    val bitmap = CatalogFragment.base64ToBitmap(equipmentImageUrl!!)
                    itemImage.setImageBitmap(bitmap)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to load item: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun saveReservation() {
        val user = auth.currentUser
        if (user == null) {
            Toast.makeText(requireContext(), "You must be logged in to reserve.", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("users").document(user.uid)
            .get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    Toast.makeText(requireContext(), "User details not found.", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                val name = document.getString("name") ?: "Unknown"
                val email = document.getString("email") ?: "Unknown"
                val comment = commentEditText.text.toString().trim()

                val reservationData = hashMapOf(
                    "userId" to user.uid,
                    "userName" to name,
                    "userEmail" to email,
                    "equipmentId" to equipmentId,
                    "equipmentName" to equipmentName,
                    "comment" to comment,
                    "timestamp" to System.currentTimeMillis()
                )

                firestore.collection("reservations")
                    .add(reservationData)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Reservation successful!", Toast.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(requireContext(), "Failed to reserve: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to fetch user: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        @JvmStatic
        fun newInstance(equipmentId: String) =
            ReserveItemFragment().apply {
                arguments = Bundle().apply { putString("equipmentId", equipmentId) }
            }
    }
}
