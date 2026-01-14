package vcmsa.projects.kloofhillcaringapp

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.NumberFormat
import java.util.Locale

class NotifyFragment : Fragment() {

    private lateinit var tabMessages: LinearLayout
    private lateinit var tabDonations: LinearLayout
    private lateinit var tabMessagesText: TextView
    private lateinit var tabDonationsText: TextView
    private lateinit var tabUnderline: View
    private lateinit var recyclerMessages: RecyclerView
    private lateinit var recyclerDonations: RecyclerView

    private lateinit var messagesAdapter: AdminItemAdapter
    private lateinit var donationsAdapter: AdminItemAdapter

    private lateinit var tabMessagesIcon: ImageView
    private lateinit var tabDonationsIcon: ImageView

    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notify, container, false)

        // Initialize views
        tabMessages = view.findViewById(R.id.tabMessages)
        tabDonations = view.findViewById(R.id.tabDonations)
        tabMessagesText = tabMessages.findViewById(R.id.tabMessagesText)
        tabDonationsText = tabDonations.findViewById(R.id.tabDonationsText)
        tabUnderline = view.findViewById(R.id.tabUnderline)
        recyclerMessages = view.findViewById(R.id.recyclerMessages)
        recyclerDonations = view.findViewById(R.id.recyclerDonations)

        recyclerMessages.layoutManager = LinearLayoutManager(requireContext())
        recyclerDonations.layoutManager = LinearLayoutManager(requireContext())

        // Initialize adapters with delete callbacks
        messagesAdapter = AdminItemAdapter(emptyList()) { item ->
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Message")
                .setMessage("Are you sure you want to delete this message from ${item.title}?")
                .setPositiveButton("Delete") { _, _ -> deleteMessage(item) }
                .setNegativeButton("Cancel", null)
                .show()
        }

        donationsAdapter = AdminItemAdapter(emptyList()) { item ->
            AlertDialog.Builder(requireContext())
                .setTitle("Delete Donation")
                .setMessage("Are you sure you want to delete this donation from ${item.subtitle}?")
                .setPositiveButton("Delete") { _, _ -> deleteDonation(item) }
                .setNegativeButton("Cancel", null)
                .show()
        }

        recyclerMessages.adapter = messagesAdapter
        recyclerDonations.adapter = donationsAdapter

        tabMessagesIcon = tabMessages.findViewById(R.id.tabMessagesIcon)
        tabDonationsIcon = tabDonations.findViewById(R.id.tabDonationsIcon)

        // Set underline width to match one tab after layout
        view.post {
            val tabWidth = tabMessages.width
            val params = tabUnderline.layoutParams
            params.width = tabWidth
            tabUnderline.layoutParams = params
        }

        // Load data
        loadMessages()
        loadDonations()

        // Tab click listeners
        tabMessages.setOnClickListener { selectTab(isMessages = true) }
        tabDonations.setOnClickListener { selectTab(isMessages = false) }

        return view
    }

    // --- Tab selection ---
    private fun selectTab(isMessages: Boolean) {
        if (isMessages) {
            tabMessagesText.setTextColor(Color.parseColor("#003366"))
            tabDonationsText.setTextColor(Color.parseColor("#888888"))
            tabMessagesIcon.setColorFilter(Color.parseColor("#003366"))
            tabDonationsIcon.setColorFilter(Color.parseColor("#888888"))

            recyclerMessages.visibility = View.VISIBLE
            recyclerDonations.visibility = View.GONE
            tabUnderline.animate().x(tabMessages.x).setDuration(200).start()
        } else {
            tabDonationsText.setTextColor(Color.parseColor("#003366"))
            tabMessagesText.setTextColor(Color.parseColor("#888888"))
            tabDonationsIcon.setColorFilter(Color.parseColor("#003366"))
            tabMessagesIcon.setColorFilter(Color.parseColor("#888888"))

            recyclerMessages.visibility = View.GONE
            recyclerDonations.visibility = View.VISIBLE
            tabUnderline.animate().x(tabDonations.x).setDuration(200).start()
        }
    }

    // --- Load messages ---
    private fun loadMessages() {
        firestore.collection("contactMessages")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val items = result.documents.map { doc ->
                    AdminItem(
                        id = doc.id,
                        title = doc.getString("name") ?: "Unknown Sender",
                        subtitle = doc.getString("email") ?: "No email provided",
                        details = doc.getString("message") ?: "No message provided",
                        phone = doc.getString("phone"),
                        timestamp = doc.getLong("timestamp")
                    )
                }
                messagesAdapter.updateList(items)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load messages.", Toast.LENGTH_SHORT).show()
            }
    }

    // --- Delete message ---
    private fun deleteMessage(item: AdminItem) {
        firestore.collection("contactMessages").document(item.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Message deleted", Toast.LENGTH_SHORT).show()
                messagesAdapter.removeItem(item)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to delete message", Toast.LENGTH_SHORT).show()
            }
    }

    // --- Load donations ---
    private fun loadDonations() {
        val donationsRef = firestore.collection("donations")
        val usersRef = firestore.collection("users")

        donationsRef.orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { donationSnapshot ->

                val donationList = mutableListOf<AdminItem>()
                val userCache = mutableMapOf<String, String>()
                val totalDocs = donationSnapshot.size()
                var processedDocs = 0

                val currencyFormat = NumberFormat.getNumberInstance(Locale("af", "ZA")).apply {
                    minimumFractionDigits = 2
                    maximumFractionDigits = 2
                }

                for (doc in donationSnapshot.documents) {
                    val docId = doc.id
                    val userId = doc.getString("userId") ?: "anonymous"
                    val amount = doc.getDouble("amount") ?: 0.0
                    val message = doc.getString("message") ?: ""
                    val timestamp = (doc.get("timestamp") as? com.google.firebase.Timestamp)?.toDate()?.time

                    val formattedAmount = "R${currencyFormat.format(amount)}"

                    fun addDonationItem(userName: String) {
                        donationList.add(
                            AdminItem(
                                id = docId,
                                title = formattedAmount,
                                subtitle = "Donor: $userName",
                                details = message,
                                timestamp = timestamp
                            )
                        )
                        processedDocs++
                        if (processedDocs == totalDocs) {
                            donationsAdapter.updateList(donationList)
                        }
                    }

                    if (userCache.containsKey(userId)) {
                        addDonationItem(userCache[userId] ?: "Unknown")
                    } else {
                        usersRef.document(userId).get()
                            .addOnSuccessListener { userDoc ->
                                val userName = userDoc.getString("name") ?: "Unknown"
                                userCache[userId] = userName
                                addDonationItem(userName)
                            }
                            .addOnFailureListener {
                                addDonationItem("Unknown")
                            }
                    }
                }

                if (donationSnapshot.isEmpty) {
                    donationsAdapter.updateList(emptyList())
                }

            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load donations.", Toast.LENGTH_SHORT).show()
            }
    }

    // --- Delete donation ---
    private fun deleteDonation(item: AdminItem) {
        firestore.collection("donations").document(item.id)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Donation deleted", Toast.LENGTH_SHORT).show()
                donationsAdapter.removeItem(item)
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to delete donation", Toast.LENGTH_SHORT).show()
            }
    }
}
