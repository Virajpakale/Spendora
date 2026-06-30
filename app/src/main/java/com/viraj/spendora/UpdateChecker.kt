package com.viraj.spendora

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley

class UpdateChecker(private val context: Context) {

    fun checkForUpdates() {
        val url =
            "https://api.github.com/repos/Virajpakale/Spendora/releases/latest"

        val request = JsonObjectRequest(
            url,
            { response ->
                val latestVersion = response.getString("tag_name")
                val currentVersion = "v1.0"

                if (latestVersion != currentVersion) {
                    showUpdateDialog(latestVersion)
                }
            },
            {
                // Ignore errors
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
                        "https://github.com/YOUR_USERNAME/Spendora/releases/latest"
                    )
                )
                context.startActivity(intent)
            }
            .setNegativeButton("Later", null)
            .show()
    }
}