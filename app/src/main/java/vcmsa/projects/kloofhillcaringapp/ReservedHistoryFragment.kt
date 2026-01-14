package vcmsa.projects.kloofhillcaringapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ReservedHistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var reservationAdapter: UserReservationAdapter
    private val reservations = mutableListOf<Reservation>()
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var backArrow: ImageView
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reserved_history, container, false)

        recyclerView = view.findViewById(R.id.reservedRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        reservationAdapter = UserReservationAdapter(reservations)
        recyclerView.adapter = reservationAdapter

        // Initialize back arrow
        backArrow = view.findViewById(R.id.backArrow)
        backArrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        loadReservations()

        return view
    }

    private fun loadReservations() {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            Toast.makeText(requireContext(), "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        firestore.collection("reservations")
            .whereEqualTo("userId", uid)
            .get()
            .addOnSuccessListener { snapshot ->
                reservations.clear()
                for (doc in snapshot.documents) {
                    val itemName = doc.getString("equipmentName") ?: ""
                    val timestamp = doc.getLong("timestamp") ?: 0L

                    val reservation = Reservation(
                        itemName = itemName,
                        timestamp = timestamp
                    )

                    reservations.add(reservation)
                }

                reservations.sortByDescending { it.timestamp }
                reservationAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to load reservations: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
