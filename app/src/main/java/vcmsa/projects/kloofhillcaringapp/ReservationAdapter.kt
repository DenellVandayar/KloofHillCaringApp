package vcmsa.projects.kloofhillcaringapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

data class Reservation(
    val itemName: String = "",
    val userName: String = "",
    val userEmail: String = "",
    val comment: String = "",
    val timestamp: Long = 0L,
    var docId: String? = null
)

class ReservationAdapter(
    private val reservations: MutableList<Reservation>,
    private val onDeleteClick: (Reservation) -> Unit
) : RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReservationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reservation, parent, false)
        return ReservationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReservationViewHolder, position: Int) {
        val reservation = reservations[position]
        holder.itemName.text = reservation.itemName
        holder.userName.text = "User: ${reservation.userName}"
        holder.userEmail.text = "Email: ${reservation.userEmail}"
        holder.comment.text = "Comment: ${reservation.comment}"
        holder.timestamp.text = dateFormat.format(Date(reservation.timestamp))

        // Set delete button click
        holder.deleteBtn.setOnClickListener {
            onDeleteClick(reservation)
        }
    }

    override fun getItemCount(): Int = reservations.size

    class ReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.resItemName)
        val userName: TextView = itemView.findViewById(R.id.resUserName)
        val userEmail: TextView = itemView.findViewById(R.id.resUserEmail)
        val comment: TextView = itemView.findViewById(R.id.resComment)
        val timestamp: TextView = itemView.findViewById(R.id.resTimestamp)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.deleteBtn) // make sure this exists in item_reservation.xml
    }
}
