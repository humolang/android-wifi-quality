package com.humolang.wifiless.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.KeyboardArrowDown
import androidx.compose.material.icons.twotone.KeyboardArrowUp
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.humolang.wifiless.R
import com.humolang.wifiless.data.datasources.DEFAULT_HEAT_ID
import com.humolang.wifiless.data.datasources.UNKNOWN
import com.humolang.wifiless.data.datasources.UNKNOWN_LINK_SPEED
import com.humolang.wifiless.data.datasources.UNKNOWN_RSSI
import com.humolang.wifiless.data.datasources.model.WifiCapabilities
import com.humolang.wifiless.data.datasources.model.WifiProperties
import com.humolang.wifiless.ui.screens.components.GraphDrawer
import com.humolang.wifiless.ui.states.LinkSpeedGraphState
import com.humolang.wifiless.ui.states.RssiGraphState
import com.humolang.wifiless.ui.viewmodels.StartViewModel
import kotlinx.coroutines.flow.StateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StartScreen(
    navigateToSettings: () -> Unit,
    navigateToPlanning: (Long) -> Unit,
    navigateToHeats: () -> Unit,
    startViewModel: StartViewModel =
        viewModel(factory = StartViewModel.Factory)
) {
    val scrollBehavior = TopAppBarDefaults
        .pinnedScrollBehavior()

    val snackbarHostState = remember {
        SnackbarHostState()
    }

    val isWifiEnabled by startViewModel
        .isWifiEnabled.collectAsStateWithLifecycle()

    if (!isWifiEnabled) {
        val message = stringResource(
            id = R.string.wifi_is_off
        )

        LaunchedEffect(key1 = isWifiEnabled) {
            snackbarHostState.showSnackbar(
                message = message
            )
        }
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(
                scrollBehavior.nestedScrollConnection
            ),
        topBar = {
            StartTopBar(
                navigateToSettings = navigateToSettings,
                scrollBehavior = scrollBehavior
            )
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        content = { innerPadding ->
            StartContent(
                navigateToPlanning = navigateToPlanning,
                navigateToHeats = navigateToHeats,
                startViewModel = startViewModel,
                modifier = Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StartTopBar(
    navigateToSettings: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.twotone_app_icon_24),
                    contentDescription = null
                )
                Text(
                    stringResource(id = R.string.app_name),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        },
        actions = {
            IconButton(
                onClick = navigateToSettings
            ) {
                Icon(
                    imageVector = Icons.TwoTone.Settings,
                    contentDescription = stringResource(
                        id = R.string.settings
                    )
                )
            }
        },
        scrollBehavior = scrollBehavior,
        modifier = modifier
    )
}

@Composable
private fun StartContent(
    navigateToPlanning: (Long) -> Unit,
    navigateToHeats: () -> Unit,
    startViewModel: StartViewModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(16.dp)) {

        RssiGraph(
            dequeCapacity = startViewModel.dequeCapacity,
            rssiGraphState = startViewModel.rssiGraphState,
            latestRssi = startViewModel.latestRssi,
            rssiValues = startViewModel.rssiValues,
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        )
        LinkSpeedGraph(
            dequeCapacity = startViewModel.dequeCapacity,
            linkSpeedGraphState = startViewModel.linkSpeedGraphState,
            latestLinkSpeed = startViewModel.latestLinkSpeed,
            linkSpeedUnits = startViewModel.linkSpeedUnits,
            linkSpeedValues = startViewModel.linkSpeedValues,
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        )
        ToolsButtons(
            navigateToPlanning = navigateToPlanning,
            navigateToHeats = navigateToHeats,
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        )
        WifiCapabilities(
            wifiCapabilities = startViewModel.wifiCapabilities,
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        )
        WifiProperties(
            wifiProperties = startViewModel.wifiProperties,
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        )
    }
}

@Composable
private fun RssiGraph(
    dequeCapacity: Int,
    rssiGraphState: StateFlow<RssiGraphState>,
    latestRssi: StateFlow<Int>,
    rssiValues: StateFlow<ArrayDeque<Int>>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            var expanded by rememberSaveable {
                mutableStateOf(true)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.rssi),
                    modifier = Modifier,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.weight(1f))

                val rssi by latestRssi
                    .collectAsStateWithLifecycle()

                Text(
                    text = if (rssi != UNKNOWN_RSSI) {
                        stringResource(
                            id = R.string.rssi_dbm,
                            rssi
                        )
                    } else {
                        stringResource(id = R.string.unknown)
                    },
                    modifier = Modifier,
                    style = MaterialTheme.typography.titleLarge
                )

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    if (expanded) {
                        Icon(
                            imageVector = Icons.TwoTone.KeyboardArrowUp,
                            contentDescription = stringResource(
                                id = R.string.minimize
                            )
                        )
                    } else {
                        Icon(
                            imageVector = Icons.TwoTone.KeyboardArrowDown,
                            contentDescription = stringResource(
                                id = R.string.expand
                            )
                        )
                    }
                }
            }

            if (expanded) {
                val graphState by rssiGraphState
                    .collectAsStateWithLifecycle()

                val points by rssiValues
                    .collectAsStateWithLifecycle()

                GraphDrawer(
                    points = points,
                    pointsCapacity = dequeCapacity,
                    horizontalLimit = graphState.rssiHorizontalCapacity,
                    verticalLimit = graphState.minRssi,
                    isPositive = false,
                    labelX = stringResource(id = R.string.label_x_time),
                    labelY = stringResource(id = R.string.label_y_rssi),
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .aspectRatio(3 / 2f)
                )
            }
        }
    }
}

@Composable
private fun LinkSpeedGraph(
    dequeCapacity: Int,
    linkSpeedGraphState: StateFlow<LinkSpeedGraphState>,
    latestLinkSpeed: StateFlow<Int>,
    linkSpeedValues: StateFlow<ArrayDeque<Int>>,
    linkSpeedUnits: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            var expanded by rememberSaveable {
                mutableStateOf(false)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.link_speed),
                    modifier = Modifier,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.weight(1f))

                val linkSpeed by latestLinkSpeed
                    .collectAsStateWithLifecycle()

                Text(
                    text = if (linkSpeed != UNKNOWN_LINK_SPEED) {
                        stringResource(
                            id = R.string.link_speed_value,
                            linkSpeed,
                            linkSpeedUnits
                        )
                    } else {
                        stringResource(id = R.string.unknown)
                    },
                    modifier = Modifier,
                    style = MaterialTheme.typography.titleLarge
                )

                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    if (expanded) {
                        Icon(
                            imageVector = Icons.TwoTone.KeyboardArrowUp,
                            contentDescription = stringResource(
                                id = R.string.minimize
                            )
                        )
                    } else {
                        Icon(
                            imageVector = Icons.TwoTone.KeyboardArrowDown,
                            contentDescription = stringResource(
                                id = R.string.expand
                            )
                        )
                    }
                }
            }

            if (expanded) {
                val graphState by linkSpeedGraphState
                    .collectAsStateWithLifecycle()

                val points by linkSpeedValues
                    .collectAsStateWithLifecycle()

                GraphDrawer(
                    points = points,
                    pointsCapacity = dequeCapacity,
                    horizontalLimit = graphState.linkSpeedHorizontalCapacity,
                    verticalLimit = graphState.maxLinkSpeed,
                    isPositive = true,
                    labelX = stringResource(id = R.string.label_x_time),
                    labelY = stringResource(
                        id = R.string.label_y_link_speed,
                        linkSpeedUnits
                    ),
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .aspectRatio(3 / 2f)
                )
            }
        }
    }
}

@Composable
private fun ToolsButtons(
    navigateToPlanning: (Long) -> Unit,
    navigateToHeats: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.tools_buttons),
                style = MaterialTheme.typography.titleLarge
            )

            Row(modifier = Modifier.padding(top = 8.dp)) {
                val buttonShape = MaterialTheme.shapes.small

                ElevatedButton(
                    onClick = {
                        navigateToPlanning(DEFAULT_HEAT_ID)
                    },
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .weight(1f),
                    shape = buttonShape
                ) {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.twotone_architecture_24
                            ),
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(id = R.string.room_plan),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }

                ElevatedButton(
                    onClick = navigateToHeats,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    shape = buttonShape
                ) {
                    Row(
                        modifier = Modifier,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(
                                id = R.drawable.twotone_map_24
                            ),
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(id = R.string.heatmaps),
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WifiCapabilities(
    wifiCapabilities: StateFlow<WifiCapabilities>,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            var expanded by rememberSaveable {
                mutableStateOf(true)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.capabilities),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    if (expanded) {
                        Icon(
                            imageVector = Icons.TwoTone.KeyboardArrowUp,
                            contentDescription = stringResource(
                                id = R.string.minimize
                            )
                        )
                    } else {
                        Icon(
                            imageVector = Icons.TwoTone.KeyboardArrowDown,
                            contentDescription = stringResource(
                                id = R.string.expand
                            )
                        )
                    }
                }
            }

            if (expanded) {
                Column(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    val textStyle = MaterialTheme
                        .typography
                        .bodyLarge

                    val capabilities by wifiCapabilities
                        .collectAsStateWithLifecycle()

                    InfoRow(
                        labelText = stringResource(
                            id = R.string.wifi_standard
                        ),
                        infoText = stringResource(
                            capabilities.wifiStandardStringId
                        ),
                        textStyle = textStyle,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    InfoRow(
                        labelText = stringResource(
                            id = R.string.security_type
                        ),
                        infoText = stringResource(
                            id = capabilities.securityTypeStringId
                        ),
                        textStyle = textStyle,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    InfoRow(
                        labelText = stringResource(
                            id = R.string.frequency
                        ),
                        infoText = stringResource(
                            id = R.string.frequency_value,
                            capabilities.frequency,
                            capabilities.frequencyUnits
                        ),
                        textStyle = textStyle,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    InfoRow(
                        labelText = stringResource(
                            id = R.string.supported_frequencies
                        ),
                        infoText = supportedFrequencies(capabilities),
                        textStyle = textStyle,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    InfoRow(
                        labelText = stringResource(
                            id = R.string.downstream_bandwidth
                        ),
                        infoText = stringResource(
                            id = R.string.bandwidth_kbps,
                            capabilities.downstreamBandwidthKbps
                        ),
                        textStyle = textStyle,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    InfoRow(
                        labelText = stringResource(
                            id = R.string.upstream_bandwidth
                        ),
                        infoText = stringResource(
                            id = R.string.bandwidth_kbps,
                            capabilities.upstreamBandwidthKbps
                        ),
                        textStyle = textStyle,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun supportedFrequencies(
    capabilities: WifiCapabilities
): String {
    var supportedFrequencies = UNKNOWN

    if (capabilities.is24GHzSupported) {
        supportedFrequencies = stringResource(
            id = R.string.supported24ghz
        )
    }
    if (capabilities.is5GHzSupported) {
        supportedFrequencies = if (supportedFrequencies != UNKNOWN) {
            "$supportedFrequencies / ${stringResource(
                id = R.string.supported5ghz
            )}"
        } else {
            stringResource(
                id = R.string.supported5ghz
            )
        }
    }
    if (capabilities.is6GHzSupported) {
        supportedFrequencies = if (supportedFrequencies != UNKNOWN) {
            "$supportedFrequencies / ${stringResource(
                id = R.string.supported6ghz
            )}"
        } else {
            stringResource(
                id = R.string.supported6ghz
            )
        }
    }
    if (capabilities.is60GHzSupported) {
        supportedFrequencies = if (supportedFrequencies != UNKNOWN) {
            "$supportedFrequencies / ${stringResource(
                id = R.string.supported60ghz
            )}"
        } else {
            stringResource(
                id = R.string.supported60ghz
            )
        }
    }

    return supportedFrequencies
}

@Composable
private fun WifiProperties(
    wifiProperties: StateFlow<WifiProperties>,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            var expanded by rememberSaveable {
                mutableStateOf(true)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(id = R.string.properties),
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    if (expanded) {
                        Icon(
                            imageVector = Icons.TwoTone.KeyboardArrowUp,
                            contentDescription = stringResource(
                                id = R.string.minimize
                            )
                        )
                    } else {
                        Icon(
                            imageVector = Icons.TwoTone.KeyboardArrowDown,
                            contentDescription = stringResource(
                                id = R.string.expand
                            )
                        )
                    }
                }
            }

            if (expanded) {
                Column(
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    val textStyle = MaterialTheme
                        .typography
                        .bodyLarge

                    val properties by wifiProperties
                        .collectAsStateWithLifecycle()

                    InfoRow(
                        labelText = stringResource(
                            id = R.string.ipv4_address
                        ),
                        infoText = properties.ipv4Address,
                        textStyle = textStyle,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    InfoRow(
                        labelText = stringResource(
                            id = R.string.ipv6_address
                        ),
                        infoText = properties.ipv6Address,
                        textStyle = textStyle,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    InfoRow(
                        labelText = stringResource(
                            id = R.string.interface_name
                        ),
                        infoText = properties.interfaceName,
                        textStyle = textStyle,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    InfoRow(
                        labelText = stringResource(
                            id = R.string.dhcp_server_address
                        ),
                        infoText = properties.dhcpServer,
                        textStyle = textStyle,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    InfoRow(
                        labelText = stringResource(
                            id = R.string.dns_server
                        ),
                        infoText = properties.dnsServer,
                        textStyle = textStyle,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    labelText: String,
    infoText: String,
    textStyle: TextStyle,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
    ) {
        Text(
            text = labelText,
            modifier = Modifier
                .padding(end = 8.dp)
                .weight(1f),
            style = textStyle
        )
        Text(
            text = infoText,
            modifier = Modifier
                .padding(start = 8.dp)
                .weight(1f),
            style = textStyle
        )
    }
}