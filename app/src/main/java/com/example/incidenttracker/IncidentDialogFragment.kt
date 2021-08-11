package com.example.incidenttracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class IncidentDialogFragment : DialogFragment() {

    private lateinit var binding: FragmentDialogBinding
    lateinit var db: DocumentReference
    var incidentText: String? = null
    var incidentDescription: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentDialogBinding.inflate(layoutInflater)
        db = FirebaseFirestore.getInstance().document("reports/incident")
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                incidentText= parent?.getItemAtPosition(position).toString()
            }
        }
        incidentDescription = binding.descriptionEditText.text.toString()
        binding.submitIncident.setOnClickListener { submitToFirebase() }

        return binding.root
    }

    private fun submitToFirebase() {
        if (!incidentText?.isEmpty()!! || !incidentDescription?.isEmpty()!!){
            try {
                val items = HashMap<String, Any>()
                items.put("incidentText1", incidentText!!)
                items.put("incidentDescription1", incidentDescription!!)
                db.collection("reports").document("incident").set(items).addOnSuccessListener {
                    Toast.makeText(activity, "Uploaded", Toast.LENGTH_LONG).show()
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

        @JvmStatic
        fun newInstance(param1: String, param2: String) = IncidentDialogFragment

    }
}