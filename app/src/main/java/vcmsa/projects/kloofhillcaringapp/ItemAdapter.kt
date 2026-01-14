package vcmsa.projects.kloofhillcaringapp

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class ItemAdapter(
    private val items: MutableList<Item>,
    private val isAdmin: Boolean
) : RecyclerView.Adapter<ItemAdapter.EquipmentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EquipmentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item, parent, false)
        return EquipmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: EquipmentViewHolder, position: Int) {
        val equipment = items.getOrNull(position) ?: return

        holder.name.text = equipment.name.ifEmpty { "No Name" }
        holder.desc.text = equipment.description.ifEmpty { "No Description" }
        holder.price.text = "R${String.format("%.2f", equipment.price)} per month"

        if (equipment.hasDeposit && equipment.depositAmount != null) {
            holder.deposit.text = "Deposit: R${String.format("%.2f", equipment.depositAmount)}"
            holder.deposit.visibility = View.VISIBLE
        } else {
            holder.deposit.visibility = View.GONE
        }

        holder.availability.text = if (equipment.available) "Available" else "Not Available"
        holder.availability.setTextColor(
            if (equipment.available) Color.parseColor("#4CAF50")
            else Color.parseColor("#F44336")
        )

        if (!equipment.imageBase64.isNullOrEmpty()) {
            holder.image.setImageBitmap(CatalogFragment.base64ToBitmap(equipment.imageBase64!!))
        } else {
            holder.image.setImageDrawable(null)
        }

        // Show/hide admin buttons
        holder.editBtn.visibility = if (isAdmin) View.VISIBLE else View.GONE
        holder.deleteBtn.visibility = if (isAdmin) View.VISIBLE else View.GONE

        // Reserve button: only visible for normal users
        if (!isAdmin) {
            holder.reserveBtn.visibility = View.VISIBLE

            // Enable/disable based on availability
            holder.reserveBtn.isEnabled = equipment.available
            holder.reserveBtn.alpha = if (equipment.available) 1.0f else 0.5f
            holder.reserveBtn.text = if (equipment.available) "Reserve" else "Reserve"

            holder.reserveBtn.setOnClickListener {
                if (!equipment.available) {
                    Toast.makeText(holder.itemView.context, "This item is currently unavailable", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val fragment = ReserveItemFragment().apply {
                    arguments = Bundle().apply {
                        putString("equipmentId", equipment.id)
                    }
                }
                (holder.itemView.context as AppCompatActivity).supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        } else {
            holder.reserveBtn.visibility = View.GONE
        }

        // Set custom icons for admin buttons
        holder.editBtn.setImageResource(R.drawable.edit)
        holder.deleteBtn.setImageResource(R.drawable.delete)

        holder.editBtn.setOnClickListener {
            if (!isAdmin) return@setOnClickListener
            val fragmentManager =
                (holder.itemView.context as? AppCompatActivity)?.supportFragmentManager ?: return@setOnClickListener

            val fragment = AddingItemsFragment().apply {
                arguments = Bundle().apply { putString("equipmentId", equipment.id) }
            }

            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }

        holder.deleteBtn.setOnClickListener {
            if (!isAdmin) return@setOnClickListener
            val context = holder.itemView.context
            val firestore = FirebaseFirestore.getInstance()

            if (equipment.id.isNotEmpty()) {
                // Show confirmation dialog
                androidx.appcompat.app.AlertDialog.Builder(context)
                    .setTitle("Confirm Delete")
                    .setMessage("Are you sure you want to delete '${equipment.name}'?")
                    .setPositiveButton("Yes") { _, _ ->
                        firestore.collection("equipment")
                            .document(equipment.id)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show()
                                val adapterPos = holder.adapterPosition
                                if (adapterPos != RecyclerView.NO_POSITION) {
                                    items.removeAt(adapterPos)
                                    notifyItemRemoved(adapterPos)
                                }
                            }
                            .addOnFailureListener {
                                Toast.makeText(context, "Failed: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            } else {
                Toast.makeText(context, "No valid ID!", Toast.LENGTH_SHORT).show()
            }
        }

        holder.itemView.setOnClickListener {
            val fragment = ItemDetailFragment().apply {
                arguments = Bundle().apply { putString("equipmentId", equipment.id) }
            }

            (holder.itemView.context as AppCompatActivity).supportFragmentManager
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun getItemCount(): Int = items.size

    class EquipmentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.findViewById(R.id.itemImage)
        val name: TextView = itemView.findViewById(R.id.itemName)
        val desc: TextView = itemView.findViewById(R.id.itemDesc)
        val price: TextView = itemView.findViewById(R.id.itemPrice)
        val deposit: TextView = itemView.findViewById(R.id.itemDeposit)
        val availability: TextView = itemView.findViewById(R.id.itemAvailability)
        val editBtn: ImageButton = itemView.findViewById(R.id.editBtn)
        val deleteBtn: ImageButton = itemView.findViewById(R.id.deleteBtn)
        val reserveBtn: com.google.android.material.button.MaterialButton = itemView.findViewById(R.id.reserveBtn)
    }
}
