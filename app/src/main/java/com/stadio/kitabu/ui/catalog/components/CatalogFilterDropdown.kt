package com.stadio.kitabu.ui.catalog.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.stadio.kitabu.ui.catalog.CatalogFilter

private fun CatalogFilter.label(): String {
    return when (this) {
        CatalogFilter.ALL -> "All books"
        CatalogFilter.AVAILABLE -> "Available books"
        CatalogFilter.BORROWED -> "Borrowed books"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogFilterDropdown(
    selected: CatalogFilter,
    onFilterSelected: (CatalogFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected.label(),
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .menuAnchor(
                    type = MenuAnchorType.PrimaryNotEditable,
                    enabled = true
                )
                .fillMaxWidth(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            CatalogFilter.entries.forEach { option ->
                DropdownMenuItem(
                    text = {
                        Text(option.label())
                    },
                    onClick = {
                        onFilterSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}