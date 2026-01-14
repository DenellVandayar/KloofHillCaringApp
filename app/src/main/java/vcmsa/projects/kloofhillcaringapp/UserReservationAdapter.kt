package vcmsa.projects.kloofhillcaringapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class UserReservationAdapter(
    private val reservations: List<Reservation>
) : RecyclerView.Adapter<UserReservationAdapter.UserReservationViewHolder>() {

    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserReservationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_reserved_history, parent, false)
        return UserReservationViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserReservationViewHolder, position: Int) {
        val reservation = reservations[position]
        // Use itemName from Reservation class
        holder.itemName.text = reservation.itemName
        holder.timestamp.text = dateFormat.format(Date(reservation.timestamp))
    }

    override fun getItemCount(): Int = reservations.size

    class UserReservationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.resItemName)
        val timestamp: TextView = itemView.findViewById(R.id.resTimestamp)
    }
}
