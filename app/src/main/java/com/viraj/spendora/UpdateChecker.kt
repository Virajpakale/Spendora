package com.viraj.spendora

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class UpdateChecker(private val context: Context) {

    fun checkForUpdates() {
        val url =
            "https://api.github.com/repos/Virajpakale/Spendora/releases/latest"

        val request = JsonObjectRequest(
            url,
            { response ->

                val latestVersionName = response.getString("tag_name")

                // Example: v1.4 -> 14
                val latestVersionCode =
                    latestVersionName.replace("v", "")
                        .replace(".", "")
                        .toLong()

                val packageInfo =
                    context.packageManager.getPackageInfo(
                        context.packageName,
                        0
                    )

                val currentVersionCode =
                    packageInfo.longVersionCode

                Toast.makeText(
                    context,
                    "Latest: $latestVersionCode | Current: $currentVersionCode",
                    Toast.LENGTH_LONG
                ).show()

                if (latestVersionCode > currentVersionCode) {
                    showUpdateDialog(latestVersionName)
                }

            },
            { error ->
                Toast.makeText(
                    context,
                    "Update failed: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )

        Volley.newRequestQueue(context).add(request)
    }

    private fun showUpdateDialog(version: String) {
        AlertDialog.Builder(context)
            .setTitle("Update Available 🚀")
            .setMessage("New version $version is available.")
            .setPositiveButton("Update") { _, _ ->
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(
                        "https://github.com/Virajpakale/Spendora/releases/latest"
                    )
                )
                context.startActivity(intent)
            }
            .setNegativeButton("Later", null)
            .show()
    }
}