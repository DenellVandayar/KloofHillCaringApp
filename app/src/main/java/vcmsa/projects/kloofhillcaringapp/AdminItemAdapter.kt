package vcmsa.projects.kloofhillcaringapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AdminItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val details: String,
    val phone: String? = null,
    val timestamp: Long? = null
)

class AdminItemAdapter(
    private var itemList: List<AdminItem>,
    private val onDeleteClick: (AdminItem) -> Unit
) : RecyclerView.Adapter<AdminItemAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleText: TextView = itemView.findViewById(R.id.itemTitle)
        val subtitleText: TextView = itemView.findViewById(R.id.itemSubtitle)
        val detailsText: TextView = itemView.findViewById(R.id.itemDetails)
        val phoneText: TextView = itemView.findViewById(R.id.itemPhone)
        val timestampText: TextView = itemView.findViewById(R.id.itemTimestamp)
        val deleteButton: ImageView = itemView.findViewById(R.id.itemDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_admin_card, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]

        holder.titleText.text = item.title
        holder.subtitleText.text = item.subtitle

        // --- Handle details visibility ---
        if (item.details.isBlank()) {
            holder.detailsText.visibility = View.GONE
        } else {
            holder.detailsText.visibility = View.VISIBLE
            holder.detailsText.text = item.details
        }

        // --- Handle phone visibility ---
        if (item.phone.isNullOrBlank()) {
            holder.phoneText.visibility = View.GONE
        } else {
            holder.phoneText.visibility = View.VISIBLE
            holder.phoneText.text = item.phone
        }

        // --- Donation formatting (amount detection) ---
        if (item.title.startsWith("R")) {
            holder.titleText.textSize = 30f
            holder.titleText.setTextColor(Color.parseColor("#0A3161"))
            holder.titleText.setPadding(0, 4, 0, 2)
        } else {
            holder.titleText.textSize = 20f
            holder.titleText.setTextColor(Color.parseColor("#0A3161"))
            holder.titleText.setPadding(0, 0, 0, 0)
        }

        // --- Timestamp ---
        item.timestamp?.let { time ->
            val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            holder.timestampText.text = sdf.format(Date(time))
        } ?: run {
            holder.timestampText.text = ""
        }

        holder.deleteButton.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int = itemList.size

    fun updateList(newList: List<AdminItem>) {
        itemList = newList
        notifyDataSetChanged()
    }

    fun removeItem(item: AdminItem) {
        itemList = itemList.filter { it != item } // remove the item
        notifyDataSetChanged()
    }
}
