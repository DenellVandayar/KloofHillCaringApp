package vcmsa.projects.kloofhillcaringapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class ContactUsFragment : Fragment() {
    private lateinit var nameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var phoneEditText: EditText
    private lateinit var messageEditText: EditText
    private lateinit var submitButton: Button

    // Firebase Firestore instance
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_contact_us, container, false)

        // Bind views
        nameEditText = view.findViewById(R.id.editTextName)
        emailEditText = view.findViewById(R.id.editTextEmail)
        phoneEditText = view.findViewById(R.id.editTextPhone)
        messageEditText = view.findViewById(R.id.editTextMessage)
        submitButton = view.findViewById(R.id.btnSubmit)

        // Submit form listener
        submitButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val phone = phoneEditText.text.toString().trim()
            val message = messageEditText.text.toString().trim()

            // Validation
            if (name.isEmpty() || email.isEmpty() || message.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val contactData = hashMapOf(
                "name" to name,
                "email" to email,
                "phone" to phone,
                "message" to message,
                "timestamp" to System.currentTimeMillis()
            )

            // Send to Firestore
            db.collection("contactMessages")
                .add(contactData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Message sent successfully!", Toast.LENGTH_LONG).show()
                    clearFields()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to send message: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

        return view
    }

    // Clear input fields
    private fun clearFields() {
        nameEditText.text.clear()
        emailEditText.text.clear()
        phoneEditText.text.clear()
        messageEditText.text.clear()
    }
}