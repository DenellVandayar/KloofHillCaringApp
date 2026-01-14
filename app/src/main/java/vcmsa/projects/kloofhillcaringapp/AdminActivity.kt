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

class AdminActivity : AppCompatActivity() {
    private lateinit var profileSection: LinearLayout
    private lateinit var logoutSection: LinearLayout
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation_admin)
        profileSection = findViewById(R.id.profileSection)
        logoutSection = findViewById(R.id.logoutSection)
        val user = FirebaseAuth.getInstance().currentUser

        bottomNav.visibility = if (user != null) BottomNavigationView.VISIBLE else BottomNavigationView.GONE

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CatalogFragment())
            .commit()

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_additem -> { replaceFragment(CatalogFragment()); true }
                R.id.nav_notify -> { replaceFragment(NotifyFragment()); true }
                R.id.nav_reserved_items -> { replaceFragment(AdminReservationsFragment()); true }
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
            logout()
        }
    }

    private fun logout() {
        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
