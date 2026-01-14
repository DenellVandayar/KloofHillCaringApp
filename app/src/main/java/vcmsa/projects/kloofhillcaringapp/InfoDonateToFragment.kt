package vcmsa.projects.kloofhillcaringapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import vcmsa.projects.kloofhillcaringapp.R

class InfoDonateToFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_info_donate_to, container, false)

        // --- Back arrow logic ---
        val backArrow = rootView.findViewById<ImageButton>(R.id.backArrow)
        backArrow?.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        // -------------------------

        return rootView
    }
}
