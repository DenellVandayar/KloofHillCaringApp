package vcmsa.projects.kloofhillcaringapp

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var profileSection: LinearLayout
    private lateinit var logoutSection: LinearLayout
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        profileSection = findViewById(R.id.profileSection)
        logoutSection = findViewById(R.id.logoutSection)

        val user = FirebaseAuth.getInstance().currentUser

        bottomNav.visibility = if (user != null) BottomNavigationView.VISIBLE else BottomNavigationView.GONE

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .commit()

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> { replaceFragment(HomeFragment()); true }
                R.id.nav_catalog -> { replaceFragment(CatalogFragment()); true }
                R.id.nav_location -> { replaceFragment(LocationFragment()); true }
                R.id.nav_donate -> { replaceFragment(DonationFragment()); true }
                R.id.nav_contactus -> { replaceFragment(ContactUsFragment()); true }
                else -> false
            }
        }

        // Profile section click listener (icon + text)
        profileSection.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ProfileFragment())
                .addToBackStack(null)
                .commit()
        }

        // Logout section click listener (icon + text)
        logoutSection.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, Login::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        intent.data?.let { uri ->
            val prefs = getSharedPreferences("donation", 0)
            when (uri.host) {
                "success" -> {
                    val amount = prefs.getFloat("amount", 0f).toDouble()
                    val message = prefs.getString("message", "")

                    if (amount > 0) saveDonationToFirestore(amount, message)

                    prefs.edit().putBoolean("success", true).apply()
                    Toast.makeText(this, "Payment Successful!", Toast.LENGTH_LONG).show()
                }
                "cancel" -> {
                    Toast.makeText(this, "Payment Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun saveDonationToFirestore(amount: Double, message: String?) {
        val db = FirebaseFirestore.getInstance()
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"

        val donationData = hashMapOf(
            "userId" to userId,
            "amount" to amount,
            "message" to (message ?: ""),
            "timestamp" to com.google.firebase.Timestamp.now(),
            "status" to "success"
        )

        db.collection("donations")
            .add(donationData)
            .addOnSuccessListener {
                Toast.makeText(this, "Thank you! Donation saved.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to save donation: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
