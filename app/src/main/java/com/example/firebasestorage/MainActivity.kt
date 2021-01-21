package com.example.firebasestorage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import android.app.AlertDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import dmax.dialog.SpotsDialog

class MainActivity : AppCompatActivity() {

    lateinit var alertDialog: AlertDialog
    lateinit var storageReference: StorageReference

    companion object {
        private const val IMAGE_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        config()
        findViewById<FloatingActionButton>(R.id.fbUpload).setOnClickListener {
            setIntent()
        }
    }

    fun config() {
        alertDialog = SpotsDialog.Builder().setContext(this).build()
        storageReference = FirebaseStorage.getInstance().getReference("prod_img")
    }

    //Configura a Intent para obter a imagem da galeria
    fun setIntent() {
        val intent = Intent()
        intent.type = "image/"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Captura imagem"), IMAGE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_CODE) {
            alertDialog.show()
            val uploadTask = storageReference.putFile(data!!.data!!)
            uploadTask.continueWithTask { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Chegando", Toast.LENGTH_SHORT).show()
                }
                storageReference.downloadUrl

            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    val url = downloadUri!!.toString()
                        .substring(0, downloadUri.toString().indexOf("&token"))

                    Log.i("URL referência ", url)
                    alertDialog.dismiss()
                    Picasso.get().load(url).into(findViewById<ImageView>(R.id.ivRec))
                }
                else {
                    Log.e("Erro ao upload", task.exception.toString())
                    alertDialog.dismiss()
                    Toast.makeText(this, "Erro ao subir imagem", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}