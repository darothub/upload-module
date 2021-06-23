package com.darothub.uploadimagemodule

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import coil.load
import com.araujo.jordan.excuseme.ExcuseMe
import com.darothub.uploadimagemodule.databinding.ActivityMainBinding
import com.darothub.uploadimagemodule.helpers.activity.REQUEST_CODE
import com.darothub.uploadimagemodule.helpers.activity.checkCameraPermission
import com.darothub.uploadimagemodule.helpers.activity.saveBitmap
import com.darothub.uploadimagemodule.helpers.view.viewBinding
import com.darothub.uploadimagemodule.service.ApiCall
import com.darothub.uploadimagemodule.service.NetworkHelper
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody

class MainActivity : AppCompatActivity() {
    private val binding by viewBinding(ActivityMainBinding::inflate)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val view = binding.root
        setContentView(view)


        binding.uploadBtn.setOnClickListener {
            val bool = ExcuseMe.doWeHavePermissionFor(this, Manifest.permission.CAMERA)
            if (!bool) {
                checkPermission()
            } else {
                openImageActivity()
            }
        }

    }
    private fun checkPermission() {
        ExcuseMe.couldYouGive(this).permissionFor(
                Manifest.permission.CAMERA,
        ) {
            if (it.granted.contains(Manifest.permission.CAMERA)) {
               openImageActivity()
            } else {
                lifecycleScope.launch {
                    ExcuseMe.couldYouGive(this@MainActivity).gently(
                            "Permission Request",
                            "We need this, grant the app call permission"
                    ).permissionFor(Manifest.permission.CAMERA)
                }
            }
        }
    }

    private fun openImageActivity(){
        val galleryIntent = Intent()
        galleryIntent.type = "image/*"
        galleryIntent.action = Intent.ACTION_GET_CONTENT
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val chooser = Intent.createChooser(galleryIntent, "Photo options")
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(intent))
        startActivityForResult(chooser, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val dataBitmap = data?.extras?.get("data") as Bitmap
            Toast.makeText(this, "$resultCode $dataBitmap", Toast.LENGTH_SHORT).show()
            val file = saveBitmap(dataBitmap)

            if (file != null) {

                val reqBody = file.asRequestBody("image/jpeg".toMediaTypeOrNull())

                val requestBody: RequestBody = MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", file.name, reqBody)
                        .build()

                lifecycleScope.launch {
                    val response = NetworkHelper.remoteService.uploadImage(requestBody)
                    Log.d("RemoteResponse", "$response")
                    binding.image.load(response.payload.downloadUri)
                }

            }
        }
    }



}