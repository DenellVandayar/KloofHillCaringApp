package vcmsa.projects.kloofhillcaringapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.ImageView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class ProfileFragment : Fragment() {

    private lateinit var nameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var phoneTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var editButton: MaterialButton
    private lateinit var backArrow: ImageView

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        nameTextView = view.findViewById(R.id.nameValue)
        emailTextView = view.findViewById(R.id.emailValue)
        phoneTextView = view.findViewById(R.id.phoneValue)
        addressTextView = view.findViewById(R.id.addressValue)
        editButton = view.findViewById(R.id.editProfileButton)
        backArrow = view.findViewById(R.id.backArrow)

        loadUserProfile()

        // Edit Profile
        editButton.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, EditProfileFragment())
                .addToBackStack(null)
                .commit()
        }

        // --- Back arrow logic ---
        backArrow = view.findViewById(R.id.backArrow)
        backArrow.setOnClickListener {
            // Return to the previous fragment if possible
            parentFragmentManager.popBackStack()
        }

        return view
    }

    private fun loadUserProfile() {
        val user = auth.currentUser
        if (user != null) {
            val userId = user.uid
            db.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val name = document.getString("name")
                        val email = document.getString("email")
                        val phone = document.getString("phone") ?: "Not added yet"
                        val address = document.getString("address") ?: "Not added yet"
                        nameTextView.text = name ?: "N/A"
                        emailTextView.text = email ?: "N/A"
                        phoneTextView.text = phone
                        addressTextView.text = address
                    } else {
                        Toast.makeText(requireContext(), "Profile not found", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(), "Error loading profile", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
