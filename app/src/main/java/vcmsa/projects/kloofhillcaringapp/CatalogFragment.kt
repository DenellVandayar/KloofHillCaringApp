package vcmsa.projects.kloofhillcaringapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CatalogFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var equipmentAdapter: ItemAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var auth: FirebaseAuth
    private lateinit var addEquipmentBtn: Button
    private lateinit var historyBtn: Button
    private var isAdmin = false
    private val equipmentList = mutableListOf<Item>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_catalog, container, false)

        auth = FirebaseAuth.getInstance()
        addEquipmentBtn = view.findViewById(R.id.addEquipmentBtn)
        historyBtn = view.findViewById(R.id.historyBtn)  // <-- find the history button

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        equipmentAdapter = ItemAdapter(equipmentList, isAdmin)
        recyclerView.adapter = equipmentAdapter

        // Hide buttons by default
        addEquipmentBtn.visibility = Button.GONE
        historyBtn.visibility = Button.GONE

        val uid = auth.currentUser?.uid
        if (uid != null) {
            firestore.collection("users").document(uid).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val role = document.getString("role")
                        isAdmin = role == "admin"

                        // Show buttons based on role
                        addEquipmentBtn.visibility = if (isAdmin) Button.VISIBLE else Button.GONE
                        historyBtn.visibility = if (!isAdmin) Button.VISIBLE else Button.GONE

                        equipmentAdapter = ItemAdapter(equipmentList, isAdmin)
                        recyclerView.adapter = equipmentAdapter
                    }
                    loadEquipment()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Failed to load role: ${e.message}", Toast.LENGTH_SHORT).show()
                    loadEquipment()
                }
        } else {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireContext(), Login::class.java))
        }

        // Admin: Add Item
        addEquipmentBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddingItemsFragment())
                .addToBackStack(null)
                .commit()
        }

        // Normal user: Your History
        historyBtn.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ReservedHistoryFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        loadEquipment()
    }

    private fun loadEquipment() {
        firestore.collection("equipment")
            .get()
            .addOnSuccessListener { snapshot ->
                equipmentList.clear()
                equipmentList.addAll(snapshot.toObjects(Item::class.java))
                equipmentAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to load: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        fun base64ToBitmap(base64Str: String) =
            BitmapFactory.decodeByteArray(Base64.decode(base64Str, Base64.DEFAULT), 0, Base64.decode(base64Str, Base64.DEFAULT).size)
    }
}

