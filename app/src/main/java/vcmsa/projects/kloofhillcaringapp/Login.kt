package vcmsa.projects.kloofhillcaringapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Firebase instances
        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        // Check if a user is already logged in and verified
        auth.currentUser?.let { user ->
            if (user.isEmailVerified) {
                // Already logged in: redirect immediately and finish this activity
                redirectUser(user.uid)
                return  // Skip setting the login layout
            } else {
                auth.signOut() // Ensure unverified users are logged out
            }
        }

        // Only set the layout if the user is not logged in
        setContentView(R.layout.activity_login)

        // Find views
        val emailField = findViewById<EditText>(R.id.emailField)
        val passwordField = findViewById<EditText>(R.id.passwordField)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerButton = findViewById<TextView>(R.id.signUpLink)
        val forgotPassword = findViewById<TextView>(R.id.forgotPasswordLink)

        // Login button click
        loginButton.setOnClickListener {
            val email = emailField.text.toString()
            val password = passwordField.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            if (user?.isEmailVerified == true) {
                                redirectUser(user.uid)
                            } else {
                                Toast.makeText(
                                    this,
                                    "Please verify your email!",
                                    Toast.LENGTH_LONG
                                ).show()
                                auth.signOut()
                            }
                        } else {
                            Toast.makeText(
                                this,
                                "Login failed: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(this, "Enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        // Register button click
        registerButton.setOnClickListener {
            startActivity(Intent(this, RegisterPage::class.java))
        }

        // Forgot password click
        forgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordPage::class.java))
        }
    }



    override fun onStart() {
        super.onStart()
        auth.currentUser?.let {
            redirectUser(it.uid)
        }
    }

    private fun redirectUser(uid: String) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                val role = document.getString("role")
                val intent = when (role) {
                    "admin" -> Intent(this, AdminActivity::class.java)
                    else -> Intent(this, MainActivity::class.java)
                }
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to get user role", Toast.LENGTH_LONG).show()
            }
    }
}

