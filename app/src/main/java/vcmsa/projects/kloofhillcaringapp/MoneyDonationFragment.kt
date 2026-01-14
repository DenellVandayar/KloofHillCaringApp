package vcmsa.projects.kloofhillcaringapp

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.IOException
import java.util.*
import android.widget.ImageButton


class MoneyDonationFragment : Fragment() {

    private val client = OkHttpClient()
    private val yocoSecretKey = "sk_test_960bfde0VBrLlpK098e4ffeb53e1"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_money_donation, container, false)

        val edtOtherAmount = view.findViewById<EditText>(R.id.edit_other_amount)
        val donateButton = view.findViewById<MaterialButton>(R.id.button_donate_now)

        // --- Back arrow logic ---
        val backArrow = view.findViewById<ImageButton>(R.id.backArrow)
        backArrow?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        // -------------------------

        // Predefined buttons
        val r50 = view.findViewById<MaterialButton>(R.id.button_r50)
        val r100 = view.findViewById<MaterialButton>(R.id.button_r100)
        val r200 = view.findViewById<MaterialButton>(R.id.button_r200)
        val r500 = view.findViewById<MaterialButton>(R.id.button_r500)

        // Predefined button logic
        val amountButtons = listOf(r50, r100, r200, r500)
        amountButtons.forEach { btn ->
            btn.setOnClickListener {
                edtOtherAmount.setText(btn.text.toString().replace("R", ""))
            }
        }

        // Donate button logic
        donateButton.setOnClickListener {
            val amountRands = edtOtherAmount.text.toString().toDoubleOrNull()
            if (amountRands != null && amountRands >= 2.0) {
                createCheckout(amountRands)
            } else {
                edtOtherAmount.error = "Enter at least R2.00"
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()

        val sharedPrefs = requireContext().getSharedPreferences("donation", 0)
        if (sharedPrefs.getBoolean("success", false)) {
            sharedPrefs.edit().putBoolean("success", false).apply() // reset flag
            showThankYouPopup()

            // ✅ Clear donation fields
            view?.findViewById<EditText>(R.id.edit_other_amount)?.text?.clear()
            view?.findViewById<EditText>(R.id.edit_message)?.text?.clear()
        }
    }



    private fun createCheckout(amountRands: Double) {
        val cents = (amountRands * 100).toInt()
        val jsonBody = JSONObject().apply {
            put("amount", cents)
            put("currency", "ZAR")
            put("successUrl", "kloofhillcaring://success")
            put("cancelUrl", "kloofhillcaring://cancel")
        }

        val body = jsonBody.toString()
            .toRequestBody("application/json".toMediaTypeOrNull())

        val request = Request.Builder()
            .url("https://payments.yoco.com/api/checkouts")
            .header("Authorization", "Bearer $yocoSecretKey")
            .header("Idempotency-Key", UUID.randomUUID().toString())
            .post(body)
            .build()

        CoroutineScope(Dispatchers.IO).launch {
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), "Network error. Try again.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!it.isSuccessful) {
                            println("Error creating checkout: ${it.code}")
                            requireActivity().runOnUiThread {
                                Toast.makeText(requireContext(), "Failed to start checkout.", Toast.LENGTH_SHORT).show()
                            }
                            return
                        }

                        val resp = JSONObject(it.body!!.string())
                        val checkoutUrl = resp.getString("redirectUrl")
                        println("Checkout URL: $checkoutUrl")

                        requireActivity().runOnUiThread {
                            val customTabsIntent = CustomTabsIntent.Builder().build()

                            // Save donation data locally before redirect
                            val sharedPrefs = requireContext().getSharedPreferences("donation", 0)
                            sharedPrefs.edit()
                                .putFloat("amount", amountRands.toFloat())
                                .putString(
                                    "message",
                                    view?.findViewById<EditText>(R.id.edit_message)?.text?.toString()
                                )
                                .apply()

                            customTabsIntent.launchUrl(requireContext(), Uri.parse(checkoutUrl))
                        }
                    }
                }
            })
        }
    }

    private fun showThankYouPopup() {
        AlertDialog.Builder(requireContext())
            .setTitle("Thank You ❤️")
            .setMessage("Your donation has been received successfully.\n\nWe truly appreciate your support!")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}
