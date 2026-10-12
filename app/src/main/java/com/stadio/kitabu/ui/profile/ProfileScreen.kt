package com.stadio.kitabu.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel =
        viewModel(factory = ProfileViewModel.Factory)
) {

    val activeBookingCount by
    viewModel.activeBookingCount.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Profile")
                }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Text(
                text = "STADIO Higher Education",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Surface(
                modifier = Modifier.size(96.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {

                Box(
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "Profile avatar",
                        modifier = Modifier.size(56.dp),
                        tint =
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "Sakhile Basi",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            ProfileInformationCard(
                faculty = "Information Technology",
                email = "24301530@stadio.ac.za"
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Column {

                        Text(
                            text = "Active bookings",
                            style =
                                MaterialTheme.typography.bodyLarge
                        )

                        Text(
                            text =
                                "Books currently checked out",
                            style =
                                MaterialTheme.typography.bodySmall,
                            color =
                                MaterialTheme.colorScheme
                                    .onSurfaceVariant
                        )
                    }

                    Text(
                        text =
                            activeBookingCount.toString(),
                        style =
                            MaterialTheme.typography.titleLarge,
                        color =
                            MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(
                modifier = Modifier.height(24.dp)
            )

            Button(
                onClick = {
                    // Decorative only.
                    // Profile editing is outside project scope.
                },
                modifier = Modifier.fillMaxWidth()
            ) {

                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = null
                )

                Text(
                    text = " Edit Profile"
                )
            }
        }
    }
}

@Composable
private fun ProfileInformationCard(
    faculty: String,
    email: String
) {

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {

            ProfileField(
                label = "Faculty",
                value = faculty
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            ProfileField(
                label = "Email",
                value = email
            )
        }
    }
}

@Composable
private fun ProfileField(
    label: String,
    value: String
) {

    Column {

        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color =
                MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
