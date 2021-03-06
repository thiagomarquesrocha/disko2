package com.tja.disko2.features.listplace

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.tja.disko2.db.PlaceO2Database
import com.tja.disko2.domain.PlaceO2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PlaceViewModel(activity: Activity, application: Application) : AndroidViewModel(application), ValueEventListener {

    val allPlaces: LiveData<List<PlaceO2>>
    val allPlacesFavorite: LiveData<List<PlaceO2>>
    private val placeDao = PlaceO2Database.getInstance(application).placeDao()
    private val context = application
    private val activity = activity
    private val DELIMITER = "/"

    init {
        // Get all places saved in database
        allPlaces = placeDao.getAll()
        allPlacesFavorite = placeDao.getAllFavorite()
    }

    fun initFirebase() {
        FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener {
            if (it.isSuccessful) {
                REF_LIST_PLACE.addListenerForSingleValueEvent(this)
            }
        }.addOnFailureListener {
            Log.e("COSTA", "FIREBASE LOGIN ERROR")
        }
    }

    fun favorite(placeO2: PlaceO2) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val placeAux = PlaceO2(
                placeO2.address,
                placeO2.email,
                placeO2.name,
                placeO2.phone,
                placeO2.photo,
                placeO2.type,
                placeO2.favorite,
                placeO2.key,
                placeO2.whatsapp,
                placeO2.call,
                placeO2.time,
                placeO2.description,
                placeO2.id
            )
            val value = placeDao.getPlaceByKey(placeAux.key)
            placeAux.favorite = if (value.favorite == 0) 1 else 0
            placeDao.update(placeAux)
        }
    }

    /**
     * Save all data from Firebase in LocalDB to user to use app offline
     * OBS: If data has been changed, update local DB
     */
    @Suppress("IMPLICIT_CAST_TO_ANY")
    private fun saveInDatabaseLocal(placeO2: PlaceO2) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val resultQuery = placeDao.getPlaceByKey(placeO2.key)
            if (resultQuery == null) {
                placeDao.insert(placeO2)
            } else {
                if (!hasBeenSaved(placeO2, resultQuery)) {
                    Log.d("COSTA", "Is necessary update DB =" + placeO2.name)
                    placeDao.update(updateDb(placeO2, resultQuery))
                } else {
                    Log.d("COSTA", "Is not necessary save in DB " + placeO2.name)
                }
            }
        }
    }

    /**
     * Update only fields equal to each other
     * OBS: cannot compare the favorite and id fields because FirebaseDatabase does not have those fields.
     */
    private fun updateDb(placeFirebase: PlaceO2, placeLocal: PlaceO2): PlaceO2 {
        placeLocal.name = placeFirebase.name
        placeLocal.address = placeFirebase.address
        placeLocal.phone = placeFirebase.phone
        placeLocal.photo = placeFirebase.photo
        placeLocal.type = placeFirebase.type
        placeLocal.email = placeFirebase.email
        placeLocal.call = placeFirebase.call
        placeLocal.whatsapp = placeFirebase.whatsapp
        placeLocal.time = placeFirebase.time
        placeLocal.description = placeFirebase.description
        return placeLocal
    }

    /**
     * Check if the object Place02 from FirebaseDatabase is equals with
     * object Place02 from Local Database
     * OBS: cannot compare the favorite and id fields because FirebaseDatabase does not have those fields.
     */
    private fun hasBeenSaved(placeFirebase: PlaceO2, placeLocal: PlaceO2): Boolean {
        return placeLocal.address == placeFirebase.address &&
                placeLocal.email == placeFirebase.email &&
                placeLocal.name == placeFirebase.name &&
                placeLocal.phone == placeFirebase.phone &&
                placeLocal.photo == placeFirebase.photo &&
                placeLocal.type == placeFirebase.type &&
                placeLocal.call == placeFirebase.call &&
                placeLocal.whatsapp == placeFirebase.whatsapp &&
                placeLocal.time == placeFirebase.time &&
                placeLocal.description == placeFirebase.description
    }

    /**
     * Intent to open Whatsapp
     *  Inspired on https://stackoverflow.com/questions/19081654/send-text-to-specific-contact-programmatically-whatsapp
     */
    fun intentWpp(placeO2: PlaceO2) {
        val split = placeO2.whatsapp.split(DELIMITER)
        if(split.size > 1)
            createListDialog(false, split.toTypedArray())
        else
            runIntentWhatsapp(cleanPhoneNumber(placeO2.whatsapp))
    }

    /**
     * Intent to Call Dial
     */
    fun intentCall(placeO2: PlaceO2) {
        val split = placeO2.call.split(DELIMITER)
        if(split.size > 1)
            createListDialog(true, split.toTypedArray())
        else
            runIntentCall(cleanPhoneNumber(placeO2.call))
    }

    fun runIntentCall(number: String){
        try {
            val dial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+" + number))
            dial.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(dial)
        } catch (e: java.lang.Exception) {
            Toast.makeText(context, "Não foi possível iniciar o discador.", Toast.LENGTH_LONG).show()
        }
    }

    private fun runIntentWhatsapp(number: String) {
        try {
            val i = Intent(Intent.ACTION_VIEW)
            val url = "https://wa.me/" + number
            i.setPackage("com.whatsapp")
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.data = Uri.parse(url)
            context.startActivity(i)
        } catch (e: Exception) {
            Log.e("COSTA", e.toString())
            Toast.makeText(
                context,
                "Não foi possível abrir o Whatsapp, tente ligar.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun createListDialog(isCall : Boolean, numbers: Array<String>) {
        activity.let {
            val builder = AlertDialog.Builder(it)

            builder.setTitle("Escolha um telefone")
            builder.setSingleChoiceItems(numbers, -1
            ) { dialog, which ->
                val number = cleanPhoneNumber(numbers.get(which))
                if(isCall)
                    runIntentCall(number)
                else
                    runIntentWhatsapp(number)
            }

            builder.create().show()
        }
    }

    /**
     * Clean phone number +- and white spaces
     */
    private fun cleanPhoneNumber(number: String) : String{
        return number.replace("+", "").replace(" ", "").replace("-","")
    }

    //FIREBASE CONFIG AND Callbacks
    companion object {
        private val REF_LIST_PLACE =
            FirebaseDatabase.getInstance().getReference("o2")
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        val firebasePlaces = ArrayList<String>()
        for (child in snapshot.children) {
            Log.d("COSTA", "chave = ${child.key}")
            val place = child.getValue(PlaceO2::class.java)
            place?.key = child.key.toString()
            place?.let {
                firebasePlaces.add(it.key)
                saveInDatabaseLocal(it)
            }
        }
        detelePlaces(firebasePlaces)
    }

    private fun detelePlaces(firebasePlaces: java.util.ArrayList<String>) {
        val localPlaces = (allPlaces.value as ArrayList<PlaceO2>).toMutableList()
        val deletedPlaces = localPlaces.filter { !firebasePlaces.contains(it.key) }
        Log.d("COSTA", "Total deleted ${deletedPlaces.size}")
        for (child in deletedPlaces){
            deletePlace(child)
        }
    }

    private fun deletePlace(child: PlaceO2) =  viewModelScope.launch {
        withContext(Dispatchers.IO) {
            Log.d("COSTA", "chave deleted = ${child.key}")
            placeDao.delete(child)
        }
    }

    override fun onCancelled(error: DatabaseError) {

    }
}