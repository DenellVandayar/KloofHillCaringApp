package vcmsa.projects.kloofhillcaringapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AdminReservationsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private val reservationList = mutableListOf<Reservation>()
    private lateinit var adapter: ReservationAdapter
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_reservations, container, false)

        recyclerView = view.findViewById(R.id.reservationsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Pass delete callback to adapter
        adapter = ReservationAdapter(reservationList) { reservation ->
            deleteReservation(reservation)
        }
        recyclerView.adapter = adapter

        loadReservations()

        return view
    }

    private fun loadReservations() {
        firestore.collection("reservations")
            .get()
            .addOnSuccessListener { snapshot ->
                reservationList.clear()
                for (doc in snapshot.documents) {
                    val itemName = doc.getString("equipmentName") ?: ""
                    val userName = doc.getString("userName") ?: ""
                    val userEmail = doc.getString("userEmail") ?: ""
                    val comment = doc.getString("comment") ?: ""
                    val timestampLong = doc.getLong("timestamp") ?: 0L

                    val reservation = Reservation(
                        itemName = itemName,
                        userName = userName,
                        userEmail = userEmail,
                        comment = comment,
                        timestamp = timestampLong
                    )

                    // Keep Firestore document ID for deletion
                    reservation.docId = doc.id
                    reservationList.add(reservation)
                }

                // Sort by newest first
                reservationList.sortByDescending { it.timestamp }

                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load reservations: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteReservation(reservation: Reservation) {
        val docId = reservation.docId
        if (docId.isNullOrEmpty()) return

        firestore.collection("reservations").document(docId)
            .delete()
            .addOnSuccessListener {
                reservationList.remove(reservation)
                adapter.notifyDataSetChanged()
                Toast.makeText(requireContext(), "Reservation deleted.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to delete: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    companion object {
        @JvmStatic
        fun newInstance() = AdminReservationsFragment()
    }
}
