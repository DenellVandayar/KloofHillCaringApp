package vcmsa.projects.kloofhillcaringapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordPage : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password_page)

        // Firebase instance
        auth = FirebaseAuth.getInstance()

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val continueBtn = findViewById<Button>(R.id.continueButton)
        val returnLoginBtn = findViewById<Button>(R.id.returnLoginButton)

        // Handle "Continue" button
        continueBtn.setOnClickListener {
            val email = emailInput.text.toString().trim()

            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            } else {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(
                                this,
                                "Password reset email sent to $email.\nCheck your Junk or Spam folder if you don’t see it.",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            Toast.makeText(
                                this,
                                "Error: ${task.exception?.message}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
            }
        }

        // Handle "Return to Login Page"
        returnLoginBtn.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
            finish()
        }
    }
}
