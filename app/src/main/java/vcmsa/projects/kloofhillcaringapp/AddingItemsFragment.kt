package vcmsa.projects.kloofhillcaringapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.io.ByteArrayOutputStream

class AddingItemsFragment : Fragment() {

    private lateinit var etName: EditText
    private lateinit var etDesc: EditText
    private lateinit var etPrice: EditText
    private lateinit var etGuide: EditText
    private lateinit var imageView: ImageView
    private lateinit var btnSelectImage: Button
    private lateinit var btnSave: Button
    private lateinit var switchAvailability: Switch
    private lateinit var switchHasDeposit: Switch
    private lateinit var etDeposit: EditText

    private lateinit var backArrow: ImageView

    private val firestore = FirebaseFirestore.getInstance()
    private var equipmentId: String? = null
    private var originalImageBase64: String? = null
    private var newImageBase64: String? = null

    private val PICK_IMAGE_REQUEST = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_adding_items, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- Back arrow logic ---
        backArrow = view.findViewById(R.id.backArrow)
        backArrow.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        // Existing inputs
        etName = view.findViewById(R.id.nameInput)
        etDesc = view.findViewById(R.id.descInput)
        etPrice = view.findViewById(R.id.priceInput)
        etGuide = view.findViewById(R.id.guideInput)
        imageView = view.findViewById(R.id.previewImage)
        btnSelectImage = view.findViewById(R.id.pickImageBtn)
        btnSave = view.findViewById(R.id.saveBtn)
        switchAvailability = view.findViewById(R.id.switchAvailability)

        switchHasDeposit = view.findViewById(R.id.switchHasDeposit)
        etDeposit = view.findViewById(R.id.depositAmountInput)

        arguments?.let { equipmentId = it.getString("equipmentId") }

        if (!equipmentId.isNullOrEmpty()) {
            loadExistingEquipment(equipmentId!!)
        }

        // Show or hide deposit amount field
        switchHasDeposit.setOnCheckedChangeListener { _, isChecked ->
            etDeposit.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        btnSelectImage.setOnClickListener { selectImage() }
        btnSave.setOnClickListener { saveEquipment() }
    }

    private fun loadExistingEquipment(id: String) {
        firestore.collection("equipment").document(id).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val equipment = doc.toObject(Item::class.java)
                    equipment?.let {
                        etName.setText(it.name)
                        etDesc.setText(it.description)

                        etPrice.setText(String.format("%.2f", it.price).replace('.', ','))

                        etGuide.setText(it.guide ?: "")
                        switchAvailability.isChecked = it.available

                        switchHasDeposit.isChecked = it.hasDeposit
                        etDeposit.setText(
                            if (it.depositAmount != null)
                                String.format("%.2f", it.depositAmount).replace('.', ',')
                            else ""
                        )
                        etDeposit.visibility =
                            if (it.hasDeposit) View.VISIBLE else View.GONE

                        originalImageBase64 = it.imageBase64
                        base64ToBitmap(originalImageBase64)?.let { bmp ->
                            imageView.setImageBitmap(bmp)
                        } ?: imageView.setImageDrawable(null)
                    }
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to load: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    private fun selectImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data ?: return
            val inputStream = requireContext().contentResolver.openInputStream(uri)
            var bitmap = BitmapFactory.decodeStream(inputStream)

            bitmap = compressBitmap(bitmap, 500, 500)
            imageView.setImageBitmap(bitmap)
            newImageBase64 = bitmapToBase64(bitmap)
        }
    }

    private fun compressBitmap(bitmap: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
        val ratioBitmap = bitmap.width.toFloat() / bitmap.height.toFloat()
        val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

        var finalWidth = maxWidth
        var finalHeight = maxHeight

        if (ratioMax > ratioBitmap) {
            finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
        } else {
            finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, finalWidth, finalHeight, true)
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    private fun base64ToBitmap(base64: String?): Bitmap? {
        return try {
            if (base64.isNullOrEmpty()) return null
            val bytes = Base64.decode(base64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        } catch (e: Exception) {
            null
        }
    }

    private fun saveEquipment() {
        val name = etName.text.toString().trim()
        val desc = etDesc.text.toString().trim()
        val priceText = etPrice.text.toString().trim()
        val guide = etGuide.text.toString().trim()

        if (name.isEmpty() || desc.isEmpty() || priceText.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val price = try { priceText.replace(',', '.').toDouble() } catch (e: Exception) {
            Toast.makeText(requireContext(), "Invalid price", Toast.LENGTH_SHORT).show()
            return
        }

        val hasDeposit = switchHasDeposit.isChecked
        var depositAmount: Double? = null
        if (hasDeposit) {
            val depositText = etDeposit.text.toString().trim()
            if (depositText.isEmpty()) {
                Toast.makeText(requireContext(), "Enter deposit amount", Toast.LENGTH_SHORT).show()
                return
            }
            depositAmount = try { depositText.replace(',', '.').toDouble() } catch (e: Exception) {
                Toast.makeText(requireContext(), "Invalid deposit amount", Toast.LENGTH_SHORT).show()
                return
            }
        }

        val imageBase64ToSave = newImageBase64 ?: originalImageBase64
        if (imageBase64ToSave.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Please select an image", Toast.LENGTH_SHORT).show()
            return
        }

        if (imageBase64ToSave.length > 900_000) {
            Toast.makeText(requireContext(), "Image too large", Toast.LENGTH_SHORT).show()
            return
        }

        val equipmentData = hashMapOf(
            "id" to (equipmentId ?: firestore.collection("equipment").document().id),
            "name" to name,
            "description" to desc,
            "price" to price,
            "available" to switchAvailability.isChecked,
            "imageBase64" to imageBase64ToSave,
            "guide" to guide,
            // ➕ Save deposit info
            "hasDeposit" to hasDeposit,
            "depositAmount" to depositAmount
        )

        val docRef = if (equipmentId.isNullOrEmpty()) {
            firestore.collection("equipment").document().apply { equipmentData["id"] = this.id }
        } else {
            firestore.collection("equipment").document(equipmentId!!)
        }

        docRef.set(equipmentData, SetOptions.merge())
            .addOnSuccessListener {
                Toast.makeText(
                    requireContext(),
                    if (equipmentId.isNullOrEmpty()) "Equipment added!" else "Equipment updated!",
                    Toast.LENGTH_SHORT
                ).show()
                requireActivity().supportFragmentManager.popBackStack()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
