package nl.rapsolutions.android.tvsizecalculator

import android.content.ClipDescription
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import nl.rapsolutions.android.tvsizecalculator.databinding.FragmentAboutBinding


/**
 * A simple [Fragment] subclass as the second destination in the navigation.
 */
class AboutFragment : Fragment() {

    private var _binding: FragmentAboutBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAboutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        binding.lblVersion.text = view.context.applicationInfo.

        val pm: PackageManager = view.context.packageManager
        val pkgName: String = view.context.packageName
        var pkgInfo: PackageInfo? = null
        try {
            pkgInfo = pm.getPackageInfo(pkgName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        val ver = pkgInfo!!.versionName
        binding.lblVersion.text = "v" + ver


        binding.fabMail.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)

            intent.type = ClipDescription.MIMETYPE_TEXT_PLAIN
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("android@rap-solutions.nl"))
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Contribution: TV Screen Size Calculator")
            intent.putExtra(android.content.Intent.EXTRA_TEXT, "Hello,\n\nI would like to contribute...")

            startActivity(Intent.createChooser(intent,"Send Email..."))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}