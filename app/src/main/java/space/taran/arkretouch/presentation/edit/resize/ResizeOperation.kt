package space.taran.arkretouch.presentation.edit.resize

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.IntSize
import space.taran.arkretouch.presentation.drawing.EditManager
import space.taran.arkretouch.presentation.edit.Operation
import space.taran.arkretouch.presentation.utils.resize
import java.lang.NullPointerException

class ResizeOperation(private val editManager: EditManager) : Operation {

    private lateinit var bitmap: Bitmap
    private var aspectRatio = 1f
    private val isApplied = mutableStateOf(false)

    override fun apply() {
        editManager.apply {
            addResize()
            saveRotationAfterOtherOperation()
            toggleResizeMode()
            isApplied.value = true
        }
    }

    override fun undo() {
        editManager.apply {
            if (resizes.isNotEmpty()) {
                redoResize.push(backgroundImage.value)
                backgroundImage.value = resizes.pop()
                updateAvailableDrawArea()
                restoreRotationAfterUndoOtherOperation()
                redrawEditedPaths()
            }
        }
    }

    override fun redo() {
        editManager.apply {
            if (redoResize.isNotEmpty()) {
                resizes.push(backgroundImage.value)
                saveRotationAfterOtherOperation()
                backgroundImage.value = redoResize.pop()
                updateAvailableDrawArea()
                keepEditedPaths()
            }
        }
    }

    fun init(bitmap: Bitmap) {
        this.bitmap = bitmap
        aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
        isApplied.value = false
    }

    fun isApplied() = isApplied.value

    fun resetApply() { isApplied.value = false }

    fun resizeDown(
        width: Int,
        height: Int,
        updateImage: (ImageBitmap) -> Unit
    ): IntSize {
        return try {
            var newWidth = width
            var newHeight = height
            if (width > 0) newHeight = (
                newWidth /
                    aspectRatio
                ).toInt()
            if (height > 0)
                newWidth = (newHeight * aspectRatio).toInt()
            if (newWidth > 0 && newHeight > 0) editManager.apply {
                if (
                    newWidth <= bitmap.width &&
                    newHeight <= bitmap.height
                ) {
                    val sx = newWidth.toFloat() / bitmap.width.toFloat()
                    val sy = newHeight.toFloat() / bitmap.height.toFloat()
                    val downScale = Scale(
                        sx,
                        sy
                    )
                    val imgBitmap = bitmap.resize(downScale).asImageBitmap()
                    updateImage(imgBitmap)
                }
                editManager.updateAvailableDrawArea()
            }
            IntSize(
                newWidth,
                newHeight
            )
        } catch (e: NullPointerException) {
            e.printStackTrace()
            IntSize.Zero
        }
    }

    data class Scale(
        val x: Float = 1f,
        val y: Float = 1f
    )
}