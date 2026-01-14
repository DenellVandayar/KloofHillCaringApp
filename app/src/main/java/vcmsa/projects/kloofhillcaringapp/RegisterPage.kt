package vcmsa.projects.kloofhillcaringapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterPage : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_page)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        val nameField = findViewById<EditText>(R.id.nameInput)
        val emailField = findViewById<EditText>(R.id.emailInput)
        val passwordField = findViewById<EditText>(R.id.passwordInput)
        val registerButton = findViewById<Button>(R.id.signUpBtn)
        val goToLoginButton = findViewById<TextView>(R.id.loginLink)

        registerButton.setOnClickListener {
            val name = nameField.text.toString()
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            val userId = user?.uid
                            val userMap = hashMapOf(
                                "name" to name,
                                "email" to email,
                                "role" to "user",
                                "createdAt" to System.currentTimeMillis()
                            )

                            firestore.collection("users").document(userId!!)
                                .set(userMap)
                                .addOnSuccessListener {
                                    user.sendEmailVerification()
                                        .addOnCompleteListener { verifyTask ->
                                            if (verifyTask.isSuccessful) {
                                                Toast.makeText(
                                                    this,
                                                    "Registration successful! Please check your email to verify your account.\n(If you don’t see it, check your Junk or Spam folder.)",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                                startActivity(Intent(this, Login::class.java))
                                                finish()
                                            } else {
                                                Toast.makeText(
                                                    this,
                                                    "Failed to send verification email: ${verifyTask.exception?.message}",
                                                    Toast.LENGTH_LONG
                                                ).show()
                                            }
                                        }
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        this,
                                        "Error saving user: ${e.message}",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        } else {
                            Toast.makeText(
                                this,
                                "Registration Failed: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        goToLoginButton.setOnClickListener {
            val intent = Intent(this, Login::class.java)
            startActivity(intent)
        }
    }
}
