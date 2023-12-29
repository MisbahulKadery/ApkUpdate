package com.misbahulkadery

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import java.io.File

class downloadClass(private val cTx: Context,
private val apkName:String) {
    fun pathDownload(url: String, progressListener:onProgressListner) {
        if (Build.VERSION_CODES.TIRAMISU > Build.VERSION.SDK_INT)
            if (ActivityCompat.checkSelfPermission(
                    cTx,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(cTx, "NEED STORAGE PERMISSION", Toast.LENGTH_SHORT).show()
                ActivityCompat.requestPermissions(
                    cTx as Activity,
                    arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    0
                )
                return
            }

        val destanation = cTx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
            .toString() + "/DownloadApp.apk"
        val uri = Uri.parse("file://$destanation")
        val file = File(destanation)
        if (file.exists()) file.delete()
        val downloadManager = cTx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(url)
        val request = DownloadManager.Request(downloadUri)

       // request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        request.setMimeType("application/vnd.android.package-archive")
        request.setTitle(apkName+" Updating ...")
        request.setDescription("your apk will update. and need Unknown package install permission.\n Developed By Misbahul kadery prodhan")
        request.setRequiresCharging(false)// Set if charging is required to begin the download
        request.setAllowedOverMetered(true)// Set if download is allowed on Mobile network
        request.setAllowedOverRoaming(true);
        request.setDestinationUri(uri)
        installApk(destanation, uri)
        val downloadId = downloadManager.enqueue(request)
        progressListener.onStart()

        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object : Runnable {
            @SuppressLint("Range")
            override fun run() {
                val cursor =
                    downloadManager.query(DownloadManager.Query().setFilterById(downloadId))
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    when (status) {
                        DownloadManager.STATUS_FAILED -> {
                            progressListener.onProgress(0)
                            mainHandler.removeCallbacks(this)
                        }
                        DownloadManager.STATUS_PAUSED -> {}
                        DownloadManager.STATUS_PENDING -> {}
                        DownloadManager.STATUS_RUNNING -> {
                            val total =
                                cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                            if (total >= 0) {
                                val downloaded =
                                    cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                                val progress = (downloaded * 100L / total).toInt()
                                progressListener.onProgress(progress)
                            }
                        }

                        DownloadManager.STATUS_SUCCESSFUL -> {
                            progressListener.onProgress(100)
                            progressListener.onFinish()
                            mainHandler.removeCallbacks(this)
                            return
                        }
                    }
                }
                mainHandler.postDelayed(this, 1000)

            }
        })

    }

    private fun installApk(destination: String, uri: Uri) {
        val onComplete = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                // if (intent.getAction() != null && intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                // Handle the download complete event here
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val contentUri = FileProvider.getUriForFile(
                        context,
                        context.packageName + ".provider",
                        File(destination)
                    )
                    val install = Intent(Intent.ACTION_VIEW)
                    install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true)
                    install.data = contentUri
                    context.startActivity(install)
                    context.unregisterReceiver(this)
                } else {
                    val install = Intent(Intent.ACTION_VIEW)
                    install.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    install.setDataAndType(uri, "application/vnd.android.package-archive")
                    context.startActivity(install)
                    context.unregisterReceiver(this)
                }
                //   }

            }
        }
        cTx.registerReceiver(onComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
    }
}