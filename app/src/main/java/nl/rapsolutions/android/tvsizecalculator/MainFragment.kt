package nl.rapsolutions.android.tvsizecalculator

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.Fragment
import nl.rapsolutions.android.tvsizecalculator.databinding.FragmentMainBinding
import java.text.DecimalFormat


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private val BUNDLE_DIAG_LEFT = "DIAG_LEFT"
    private val BUNDLE_DIAG_RIGHT = "DIAG_RIGHT"

    protected var editor: EditText? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ratios = resources.getTextArray(R.array.ratios)
        val spinnerArray: MutableList<String> = ArrayList()
        spinnerArray.add(0, "")
        spinnerArray.addAll(1, ratios.map { str -> str.toString() })

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            view.context, android.R.layout.simple_list_item_1, spinnerArray
        )
        binding.spinnerRight.adapter = CustomSpinnerAdapter(
            view.context,
            android.R.layout.simple_list_item_1,
            spinnerArray.toTypedArray()
        )
        binding.spinnerLeft.adapter = CustomSpinnerAdapter(
            view.context,
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.ratios)
        )
//        binding.buttonFirst.setOnClickListener {
//            findNavController().navigate(R.id.action_MainFragment_to_AboutFragment)
//        }

        val df = DecimalFormat("0.0")
        //	df.setMinimumFractionDigits(1);
        df.minimumIntegerDigits = 1

        binding.tglCmInchButton?.setOnClickListener(object : View.OnClickListener {
            override fun onClick(arg0: View) {
                updateLabels()
                convert(binding.editDiagonalLeft)
                convert(binding.editDiagonalRight)
            }

            private fun convert(diag: TextView) {
                var oldvalue = 0.0
                oldvalue = try {
                    df.parse(diag.text.toString()).toDouble()
                } catch (e: Exception) {
                    return
                }
                if (binding.tglCmInchButton.isChecked()) {
                    // In CM
                    diag.setText(df.format(oldvalue * 2.54))
                } else {
                    // In INCH
                    diag.setText(df.format(oldvalue / 2.54))
                }
            }
        })

        buildBoxes(df, binding.spinnerLeft, binding.editDiagonalLeft, binding.editWidthLeft, binding.editHeightLeft)
        buildBoxes(df, binding.spinnerRight, binding.editDiagonalRight, binding.editWidthRight, binding.editHeightRight)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        updateLabels()
    }

//    override fun performSaveInstanceState(outState: Bundle?) {
//        super.performSaveInstanceState(outState)
//
//        outState.putString(BUNDLE_DIAG_LEFT, binding.editDiagonalLeft.getText().toString())
//        outState.putString(BUNDLE_DIAG_RIGHT, binding.editDiagonalRight.getText().toString())
//    }
//
//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        super.onViewStateRestored(savedInstanceState)
//    }

    private fun buildBoxes(df: DecimalFormat, spin: Spinner?, diag: EditText?, width: EditText?, height: EditText?) {
        if (spin != null) {
            spin.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(arg0: AdapterView<*>?, arg1: View?, arg2: Int, arg3: Long) {
                    diag!!.text = diag.text
                    if ("" == spin.selectedItem.toString()) {
                        // Disable controls
                        diag.setText("")
                        diag.isEnabled = false
                        width!!.setText("")
                        width.isEnabled = false
                        height!!.setText("")
                        height.isEnabled = false
                    } else {
                        diag.isEnabled = true
                        width!!.isEnabled = true
                        height!!.isEnabled = true
                    }
                }

                override fun onNothingSelected(arg0: AdapterView<*>?) {
                    // TODO Auto-generated method stub
                }
            }
        }
        diag?.addTextChangedListener(object : MyTextWatcher() {
            override fun afterTextChanged(s: Editable) {
                if (editor != null) return
                editor = diag
                var ratio_text = spin!!.selectedItem.toString()
                if (ratio_text.contains(" - ")) {
                    ratio_text = ratio_text.substring(0, ratio_text.indexOf(" - "))
                }
                val ratio = ratio_text.split(":").toTypedArray()
                try {
                    val x = ratio[0].toDouble()
                    val y = ratio[1].toDouble()
                    val diag_orig = Math.sqrt(x * x + y * y)

                    // Update width and Height
                    try {
                        val diag_value = df.parse(s.toString()).toDouble()
                        width!!.setText(df.format(diag_value * x / diag_orig))
                        height!!.setText(df.format(diag_value * y / diag_orig))
                    } catch (e: java.lang.Exception) {
                        width!!.setText("")
                        height!!.setText("")
                    }
                } catch (ex: NumberFormatException) {
                    // Could not have parsed the values, do nothing: this should only happen if the user did not select any ratio
                }
                editor = null
            }
        })
        width?.addTextChangedListener(object : MyTextWatcher() {
            override fun afterTextChanged(s: Editable) {
                if (editor != null) return
                editor = width
                var ratio_text = spin!!.selectedItem.toString()
                if (ratio_text.contains(" - ")) {
                    ratio_text = ratio_text.substring(0, ratio_text.indexOf(" - "))
                }
                val ratio = ratio_text.split(":").toTypedArray()
                try {
                    val x = ratio[0].toDouble()
                    val y = ratio[1].toDouble()
                    val diag_orig = Math.sqrt(x * x + y * y)

                    // Update Diag and height
                    try {
                        val width_value = df.parse(s.toString()).toDouble()
                        diag!!.setText(df.format(width_value * diag_orig / x))
                        height!!.setText(df.format(width_value * y / x))
                    } catch (e: java.lang.Exception) {
                        diag!!.setText("")
                        height!!.setText("")
                    }
                } catch (ex: NumberFormatException) {
                    // Could not have parsed the values, do nothing: this should only happen if the user did not select any ratio
                }
                editor = null
            }
        })
        height?.addTextChangedListener(object : MyTextWatcher() {
            override fun afterTextChanged(s: Editable) {
                if (editor != null) return
                editor = height
                var ratio_text = spin!!.selectedItem.toString()
                if (ratio_text.contains(" - ")) {
                    ratio_text = ratio_text.substring(0, ratio_text.indexOf(" - "))
                }
                val ratio = ratio_text.split(":").toTypedArray()
                try {
                    val x = ratio[0].toDouble()
                    val y = ratio[1].toDouble()
                    val diag_orig = Math.sqrt(x * x + y * y)
                    try {
                        // Update Diag and width
                        val height_value = df.parse(s.toString()).toDouble()
                        diag!!.setText(df.format(height_value * diag_orig / y))
                        width!!.setText(df.format(height_value * x / y))
                    } catch (e: java.lang.Exception) {
                        diag!!.setText("")
                        width!!.setText("")
                    }
                } catch (ex: NumberFormatException) {
                    // Could not have parsed the values, do nothing: this should only happen if the user did not select any ratio
                }
                editor = null
            }
        })
    }


    private fun updateLabels() {
        var text = R.string.text_ToggledINCH
        if (binding.tglCmInchButton.isChecked()) {
            text = R.string.text_ToggledCM
        }
        binding.lblScreenDiagonalMeastureType.setText(text)
        binding.lblScreenWidthMeastureType.setText(text)
        binding.lblScreenHeightMeastureType.setText(text)
    }


    /**
     * MainActivity.java -
     *
     * @author R. Rap - Rap-Solutions
     */
    private abstract class MyTextWatcher : TextWatcher {
        /*
         * (non-Javadoc)
         *
         * @see android.text.TextWatcher#afterTextChanged(android.text.Editable)
         */
        abstract override fun afterTextChanged(s: Editable)

        /*
         * (non-Javadoc)
         *
         * @see
         * android.text.TextWatcher#beforeTextChanged(java.lang.CharSequence,
         * int, int, int)
         */
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            // TODO Auto-generated method stub
        }

        /*
         * (non-Javadoc)
         *
         * @see android.text.TextWatcher#onTextChanged(java.lang.CharSequence,
         * int, int, int)
         */
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            // TODO Auto-generated method stub
        }
    }

}

class CustomSpinnerAdapter(
    context: Context, textViewResourceId: Int,
    objects: Array<String>
) : ArrayAdapter<String?>(context, textViewResourceId, objects) {
    var mContext: Context
    var mTextViewResourceId: Int
    var mValues: Array<String>

    override fun getDropDownView(
        position: Int, convertView: View?,
        parent: ViewGroup
    ): View {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row = inflater.inflate(mTextViewResourceId, parent, false) as TextView

        var value = mValues[position]
        if (value.contains(" - ")) {
            val prefix = value.substring(0, value.indexOf(" - "))
            val key = value.substring(value.indexOf(" - ") + 3)
            value =
                prefix + " - " + context.getString(context.resources.getIdentifier(key, "string", context.packageName))
        }

        row.text = value

        return row
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getCustomView(position, convertView, parent)
    }

    fun getCustomView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val row = inflater.inflate(mTextViewResourceId, parent, false) as TextView

        var value = mValues[position]
        if (value.contains(" - ")) {
            value = value.substring(0, value.indexOf(" - "))
        }
        row.text = value
        return row
    }

    init {
        mContext = context
        mTextViewResourceId = textViewResourceId
        mValues = objects
    }
}