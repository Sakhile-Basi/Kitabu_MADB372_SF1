package com.stadio.kitabu.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.stadio.kitabu.R

@Composable
fun StadioHeader(
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            painter = painterResource(
                id = R.drawable.stadio_badge
            ),
            contentDescription = "STADIO logo",
            modifier = Modifier.size(26.dp)
        )

        Text(
            text = "STADIO Higher Education",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

