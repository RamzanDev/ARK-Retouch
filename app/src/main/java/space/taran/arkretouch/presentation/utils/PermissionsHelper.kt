package space.taran.arkretouch.presentation.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import space.taran.arkretouch.BuildConfig

object PermissionsHelper {
    fun writePermContract(): ActivityResultContract<String, Boolean> {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            AllFilesAccessContract()
        } else {
            ActivityResultContracts.RequestPermission()
        }
    }

    fun launchWritePerm(launcher: ActivityResultLauncher<String>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val packageUri = "package:" + BuildConfig.APPLICATION_ID
            launcher.launch(packageUri)
        } else {
            launcher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }
}

private class AllFilesAccessContract : ActivityResultContract<String, Boolean>() {
    override fun createIntent(context: Context, input: String): Intent {
        return Intent(
            Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
            Uri.parse(input)
        )
    }

    override fun parseResult(resultCode: Int, intent: Intent?) =
        Environment.isExternalStorageManager()
}