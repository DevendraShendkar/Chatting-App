package com.example.chattingapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionManager {
    companion object {
        private const val ALL_PERMISSIONS_REQUEST_CODE = 1000
    }

    fun requestAllPermissions(context: Context) {
        val permissionsToRequest = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.BLUETOOTH
            )

        val permissionsNotGranted = permissionsToRequest.filter {
            !context.isPermissionGranted(it)
        }.toTypedArray()

        if (permissionsNotGranted.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                context as Activity,
                permissionsNotGranted,
                ALL_PERMISSIONS_REQUEST_CODE
            )
        }
    }
}

fun Context.isPermissionGranted(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}
