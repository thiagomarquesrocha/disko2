package com.tja.disko2.features.listplace

import android.app.Application
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
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


class PlaceViewModel(application: Application) : AndroidViewModel(application), ValueEventListener {

    val allPlaces: LiveData<List<PlaceO2>>
    val allPlacesFavorite: LiveData<List<PlaceO2>>
    private val placeDao = PlaceO2Database.getInstance(application).placeDao()
    private val context = application

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
                placeLocal.type == placeFirebase.type
    }

    /**
     * Intent to open Whatsapp
     */
    fun intentWpp(placeO2: PlaceO2) {
        try {
            val packageManager: PackageManager = context.packageManager
            val i = Intent(Intent.ACTION_VIEW)
            val url = "https://api.whatsapp.com/send?phone=" + getNumberPhone(false, placeO2)
            i.setPackage("com.whatsapp")
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.data = Uri.parse(url)
            if (i.resolveActivity(packageManager) != null) {
                context.startActivity(i)
            } else {
                Toast.makeText(
                    context,
                    "Verifique se você tem o Whatsapp instalado.",
                    Toast.LENGTH_LONG
                ).show()
            }
        } catch (e: Exception) {
            Log.e("COSTA", e.toString())
            Toast.makeText(
                context,
                "Erro ao tentar abrir o Whatsapp, tente ligar.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    /**
     * Intent to Call Dial
     */
    fun intentCall(placeO2: PlaceO2) {
        try {
            val dial = Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + getNumberPhone(true, placeO2)))
            dial.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(dial)
        } catch (e: java.lang.Exception) {
            Toast.makeText(context, "Erro ao tentar iniciar o discador.", Toast.LENGTH_LONG).show()
        }

    }


    /**
     * Get number phone
     */
    private fun getNumberPhone(isIntentCall: Boolean, placeO2: PlaceO2): String {
        val split = placeO2.phone.split("/")
        if (!isIntentCall) {
            return split[0]
        }
        if (split.size > 1) {
            return split[1]
        }
        return split[0]
    }

    //FIREBASE CONFIG AND Callbacks
    companion object {
        private val REF_LIST_PLACE =
            FirebaseDatabase.getInstance().getReference("o2")
    }

    override fun onDataChange(snapshot: DataSnapshot) {
        for (child in snapshot.children) {
            Log.d("COSTA", "teste = ${child.key}")
            val place = child.getValue(PlaceO2::class.java)
            place?.key = child.key.toString()
            place?.let { saveInDatabaseLocal(it) }
        }
    }

    override fun onCancelled(error: DatabaseError) {

    }
}