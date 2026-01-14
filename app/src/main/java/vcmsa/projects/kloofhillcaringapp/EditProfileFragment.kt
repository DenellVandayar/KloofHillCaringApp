package vcmsa.projects.kloofhillcaringapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class EditProfileFragment : Fragment() {
    private lateinit var nameEditText: EditText
    private lateinit var phoneEditText: EditText

    private lateinit var addressEditText: EditText

    private lateinit var saveButton: Button

    private lateinit var backButton: ImageView
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_edit_profile, container, false)

        nameEditText = view.findViewById(R.id.editName)
        phoneEditText = view.findViewById(R.id.editPhone)
        addressEditText = view.findViewById(R.id.editAddress)
        saveButton = view.findViewById(R.id.saveButton)
        backButton = view.findViewById(R.id.backButton)

        val user = auth.currentUser
        if (user != null) {
            db.collection("users").document(user.uid).get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        nameEditText.setText(document.getString("name"))
                        phoneEditText.setText(document.getString("phone"))
                        addressEditText.setText(document.getString("address"))

                    }
                }
        }

        backButton.setOnClickListener {
            parentFragmentManager.popBackStack() // simply goes back to ProfileFragment
        }

        saveButton.setOnClickListener {
            val newName = nameEditText.text.toString().trim()
            val newPhone = phoneEditText.text.toString().trim()
            val newAddress = addressEditText.text.toString().trim()

            // South African phone number: exactly 10 digits starting with 0
            val saPhoneRegex = Regex("^0\\d{9}\$")

            // Validate name
            if (newName.isEmpty()) {
                Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate phone
            if (!saPhoneRegex.matches(newPhone)) {
                Toast.makeText(context, "Enter a valid 10-digit South African phone number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // If validations pass, update Firestore
            db.collection("users").document(user!!.uid)
                .update(mapOf(
                    "name" to newName,
                    "phone" to newPhone,
                    "address" to newAddress
                ))
                .addOnSuccessListener {
                    Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ProfileFragment())
                        .commit()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
        }

        return view
    }
}