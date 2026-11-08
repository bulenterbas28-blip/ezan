package com.example.data.repository

import com.example.data.model.City

object TurkishCities {
    val allCities: List<City> = listOf(
        City(1, "Adana", 37.0000, 35.3213),
        City(2, "Adıyaman", 37.7648, 38.2786),
        City(3, "Afyonkarahisar", 38.7507, 30.5567),
        City(4, "Ağrı", 39.7191, 43.0503),
        City(5, "Amasya", 40.6500, 35.8333),
        City(6, "Ankara", 39.9334, 32.8597),
        City(7, "Antalya", 36.8969, 30.7133),
        City(8, "Artvin", 41.1828, 41.8183),
        City(9, "Aydın", 37.8560, 27.8416),
        City(10, "Balıkesir", 39.6484, 27.8826),
        City(11, "Bilecik", 40.1451, 29.9799),
        City(12, "Bingöl", 38.8854, 40.4983),
        City(13, "Bitlis", 38.4006, 42.1095),
        City(14, "Bolu", 40.7350, 31.6061),
        City(15, "Burdur", 37.7203, 30.2908),
        City(16, "Bursa", 40.1885, 29.0610),
        City(17, "Çanakkale", 40.1553, 26.4142),
        City(18, "Çankırı", 40.6013, 33.6134),
        City(19, "Çorum", 40.5506, 34.9556),
        City(20, "Denizli", 37.7765, 29.0864),
        City(21, "Diyarbakır", 37.9144, 40.2306),
        City(22, "Edirne", 41.6772, 26.5557),
        City(23, "Elazığ", 38.6810, 39.2264),
        City(24, "Erzincan", 39.7500, 39.5000),
        City(25, "Erzurum", 39.9000, 41.2700),
        City(26, "Eskişehir", 39.7767, 30.5206),
        City(27, "Gaziantep", 37.0662, 37.3833),
        City(28, "Giresun", 40.9128, 38.3895),
        City(29, "Gümüşhane", 40.4600, 39.4700),
        City(30, "Hakkari", 37.5833, 43.7333),
        City(31, "Hatay", 36.4018, 36.3498),
        City(32, "Isparta", 37.7648, 30.5566),
        City(33, "Mersin", 36.8000, 34.6333),
        City(34, "İstanbul", 41.0082, 28.9784),
        City(35, "İzmir", 38.4192, 27.1287),
        City(36, "Kars", 40.6167, 43.1000),
        City(37, "Kastamonu", 41.3887, 33.7827),
        City(38, "Kayseri", 38.7312, 35.4787),
        City(39, "Kırklareli", 41.7333, 27.2167),
        City(40, "Kırşehir", 39.1425, 34.1709),
        City(41, "Kocaeli", 40.8533, 29.8815),
        City(42, "Konya", 37.8667, 32.4833),
        City(43, "Kütahya", 39.4167, 29.9833),
        City(44, "Malatya", 38.3552, 38.3095),
        City(45, "Manisa", 38.6191, 27.4289),
        City(46, "Kahramanmaraş", 37.5858, 36.9371),
        City(47, "Mardin", 37.3212, 40.7245),
        City(48, "Muğla", 37.2153, 28.3636),
        City(49, "Muş", 38.7432, 41.5064),
        City(50, "Nevşehir", 38.6244, 34.7144),
        City(51, "Niğde", 37.9667, 34.6833),
        City(52, "Ordu", 40.9839, 37.8764),
        City(53, "Rize", 41.0201, 40.5234),
        City(54, "Sakarya", 40.7569, 30.3783),
        City(55, "Samsun", 41.2928, 36.3313),
        City(56, "Siirt", 37.9333, 41.9500),
        City(57, "Sinop", 42.0231, 35.1531),
        City(58, "Sivas", 39.7477, 37.0179),
        City(59, "Tekirdağ", 40.9833, 27.5167),
        City(60, "Tokat", 40.3167, 36.5500),
        City(61, "Trabzon", 41.0015, 39.7178),
        City(62, "Tunceli", 39.1079, 39.5401),
        City(63, "Şanlıurfa", 37.1591, 38.7969),
        City(64, "Uşak", 38.6823, 29.4082),
        City(65, "Van", 38.4891, 43.4089),
        City(66, "Yozgat", 39.8181, 34.8147),
        City(67, "Zonguldak", 41.4564, 31.7987),
        City(68, "Aksaray", 38.3687, 34.0370),
        City(69, "Bayburt", 40.2552, 40.2249),
        City(70, "Karaman", 37.1759, 33.2287),
        City(71, "Kırıkkale", 39.8468, 33.5153),
        City(72, "Batman", 37.8812, 41.1294),
        City(73, "Şırnak", 37.5164, 42.4918),
        City(74, "Bartın", 41.6344, 32.3375),
        City(75, "Ardahan", 41.1105, 42.7022),
        City(76, "Iğdır", 39.9196, 44.0450),
        City(77, "Yalova", 40.6500, 29.2667),
        City(78, "Karabük", 41.2061, 32.6204),
        City(79, "Kilis", 36.7184, 37.1212),
        City(80, "Osmaniye", 37.0742, 36.2472),
        City(81, "Düzce", 40.8438, 31.1565)
    )

    val defaultCity: City = allCities.first { it.plateCode == 34 } // İstanbul

    fun findNearestCity(lat: Double, lon: Double): City {
        return allCities.minByOrNull { city ->
            val dLat = Math.toRadians(city.lat - lat)
            val dLon = Math.toRadians(city.lon - lon)
            val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(city.lat)) *
                    Math.sin(dLon / 2) * Math.sin(dLon / 2)
            val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
            c
        } ?: defaultCity
    }
}
