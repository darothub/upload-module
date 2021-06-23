package com.darothub.uploadimagemodule.helpers.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.Settings
import android.view.Gravity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.darothub.uploadimagemodule.R
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

const val REQUEST_CODE = 100

fun Activity.checkCameraPermission(): Boolean {
    if (ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.CAMERA
            )
        ) {
            val alertBuilder = AlertDialog.Builder(this)
            alertBuilder.setTitle(R.string.allow_camera)
            alertBuilder.setMessage(R.string.camera_str)
            alertBuilder.setPositiveButton(getString(R.string.ok_str)) { dialog, which ->
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CODE
                )
                return@setPositiveButton
            }
            alertBuilder.setNegativeButton(getString(R.string.cancel)) { dialog, which ->

                val settingIntent = Intent()
                settingIntent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                val uri = Uri.fromParts("package", this.packageName, null)
                settingIntent.data = uri
                startActivity(settingIntent)
                return@setNegativeButton
            }
            val alertDialog = alertBuilder.create()
            alertDialog.setOnShowListener {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.purple_200
                    )
                )
                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.purple_200
                    )
                )
            }
            alertDialog.show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CODE
            )
            return true
        }
    } else {
        return true
    }
    return false
}

fun Activity.saveBitmap(bmp: Bitmap?): File? {
    val extStorageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    var outStream: OutputStream? = null
    var file: File? = null
    val time = System.currentTimeMillis()

    val child = "JPEG_${time}_.jpg"
    // String temp = null;
    if (extStorageDirectory != null) {
        file = File(extStorageDirectory, child)
        if (file.exists()) {
            file.delete()
            file = File(extStorageDirectory, child)
        }
        try {
            outStream = FileOutputStream(file)
            bmp?.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
            outStream.flush()
            outStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    return file
}