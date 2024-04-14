package edu.pwr.navcomsys.ships.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun LoaderWrapper(
    isLoading: Boolean,
    content: @Composable () -> Unit
) {
    if (isLoading) {
        Loader()
    } else {
        content()
    }
}

@Composable
fun Loader(color: Color = MaterialTheme.colorScheme.primary) {
    Box(modifier = Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = color
        )
    }
}