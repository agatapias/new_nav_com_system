package edu.pwr.navcomsys.ships.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import edu.pwr.navcomsys.ships.extenstion.conditional
import edu.pwr.navcomsys.ships.ui.theme.Dimensions

enum class ShipButtonType {
    Primary,
    Secondary,
    Alternative
}

@Composable
fun ShipButton(
    modifier: Modifier = Modifier,
    text: String = "",
    type: ShipButtonType = ShipButtonType.Primary,
    width: Dp? = null,
    isDisabled: Boolean = false,
    onClick: () -> Unit
) {
    val textColor =
        if (type == ShipButtonType.Alternative) MaterialTheme.colorScheme.onBackground
        else MaterialTheme.colorScheme.onSecondary
    val colors = when (type) {
        ShipButtonType.Primary -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        )
        ShipButtonType.Secondary -> ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary,
        )
        ShipButtonType.Alternative -> ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
        )
    }
    val border = if (type == ShipButtonType.Alternative) BorderStroke(
        width = Dimensions.space2,
        color = MaterialTheme.colorScheme.primary
    ) else null

    Button(
        modifier = modifier
            .conditional(width != null) { width?.let { widthIn(min = it) } ?: run { modifier } }
            .conditional(width == null) { modifier.fillMaxWidth() },
        shape = CircleShape,
        colors = colors,
        border = border,
        enabled = !isDisabled,
        contentPadding = PaddingValues(horizontal = Dimensions.space22, vertical = Dimensions.space14),
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = textColor,
            maxLines = 1
        )
    }
}