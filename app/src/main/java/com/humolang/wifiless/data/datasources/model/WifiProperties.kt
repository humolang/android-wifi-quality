/*
 * Copyright (c) 2023  humolang
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.humolang.wifiless.data.datasources.model

import com.humolang.wifiless.data.datasources.UNKNOWN

data class WifiProperties(
    val ipv4Address: String = UNKNOWN,
    val ipv6Address: String = UNKNOWN,
    val interfaceName: String = UNKNOWN,
    val dhcpServer: String = UNKNOWN,
    val dnsServer: String = UNKNOWN
)
