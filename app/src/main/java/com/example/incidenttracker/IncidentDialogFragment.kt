package com.example.incidenttracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.incidenttracker.databinding.ActivityMapsBinding
import com.example.incidenttracker.databinding.FragmentDialogBinding
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.type.LatLng

class IncidentDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentDialogBinding
    lateinit var db: FirebaseFirestore
    var incidentText: String? = null
    var incidentDescription: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDialogBinding.inflate(layoutInflater)
        db = FirebaseFirestore.getInstance()
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                incidentText= parent?.getItemAtPosition(position).toString()
            }
        }
        binding.submitIncident.setOnClickListener { submitToFirebase() }

        return binding.root

    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    private fun submitToFirebase() {
        incidentDescription = binding.descriptionEditText.text.toString()
        val lat = arguments?.get(LAT_ARGS)
        val lng = arguments?.get(LNG_ARGS)
        val loc = arguments?.get(LOC_ARGS)

        if (!incidentText?.isEmpty()!! || !incidentDescription?.isEmpty()!!){
            try {
                val items = HashMap<String, Any>()
                items.put("incidentType", incidentText!!)
                items.put("incidentDescription", incidentDescription!!)
                lat?.let { items.put("incidentLat", it) }
                lng?.let { items.put("incidentLng", it) }
                loc?.let { items.put("incidentLocation", it) }
                db.collection("reports").add(items).addOnSuccessListener {
                    Toast.makeText(activity, "Incident Uploaded", Toast.LENGTH_LONG).show()
                }.addOnFailureListener {
                    exception: java.lang.Exception -> Toast.makeText(activity, exception.toString(), Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception){
                Toast.makeText(activity, e.toString(), Toast.LENGTH_LONG).show()
            }

        } else {
            Toast.makeText(activity, "Kindly fill up all fields", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        val LAT_ARGS = "latargs"
        val LNG_ARGS = "lngargs"
        val LOC_ARGS = "locargs"
        @JvmStatic
        fun newInstance(lat: Double, lng : Double, loc : String) : IncidentDialogFragment{

            val args = Bundle()
            args.putDouble(LAT_ARGS, lat)
            args.putDouble(LNG_ARGS, lng)
            args.putString(LOC_ARGS, loc)
            val fragment = IncidentDialogFragment()
            fragment.arguments = args
            return fragment
        }
    }
}