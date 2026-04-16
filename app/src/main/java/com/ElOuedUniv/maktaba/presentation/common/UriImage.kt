package com.ElOuedUniv.maktaba.presentation.common

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.ElOuedUniv.maktaba.R

@Composable
fun UriImage(
    uriString: String?,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    @DrawableRes placeholderRes: Int = R.drawable.default_book_cover
) {
    val context = LocalContext.current
    val bitmap by produceState<Bitmap?>(initialValue = null, uriString) {
        value = uriString?.let {
            try {
                context.contentResolver.openInputStream(Uri.parse(it))?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            } catch (_: Exception) {
                null
            }
        }
    }

    if (bitmap != null) {
        Image(
            bitmap = bitmap!!.asImageBitmap(),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Image(
            painter = painterResource(id = placeholderRes),
            contentDescription = contentDescription,
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}
