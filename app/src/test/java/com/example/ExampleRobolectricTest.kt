package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.repository.PrayerCalculator
import com.example.data.repository.TurkishCities
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Ezan Vakti", appName)
  }

  @Test
  fun `test 81 cities loaded`() {
    assertEquals(81, TurkishCities.allCities.size)
    val istanbul = TurkishCities.allCities.find { it.plateCode == 34 }
    assertNotNull(istanbul)
    assertEquals("İstanbul", istanbul?.name)
  }

  @Test
  fun `test prayer calculation for Istanbul`() {
    val istanbul = TurkishCities.defaultCity
    val times = PrayerCalculator.calculatePrayerTimes(
        year = 2026,
        month = 11,
        day = 7,
        lat = istanbul.lat,
        lon = istanbul.lon
    )
    assertNotNull(times)
    assertTrue(times.imsak.contains(":"))
    assertTrue(times.gunes.contains(":"))
    assertTrue(times.ogle.contains(":"))
    assertTrue(times.ikindi.contains(":"))
    assertTrue(times.aksam.contains(":"))
    assertTrue(times.yatsi.contains(":"))
    assertEquals(6, times.prayersList.size)
  }

  @Test
  fun `test Qibla calculation for Istanbul`() {
    val istanbul = TurkishCities.defaultCity
    val qibla = PrayerCalculator.calculateQiblaDirection(istanbul.lat, istanbul.lon)
    // Istanbul Qibla is approximately 151 degrees (South-East)
    assertTrue("Qibla angle should be between 145 and 155 degrees: $qibla", qibla in 145.0..155.0)
  }

  @Test
  fun `test namaz surahs and duas loaded`() {
    val items = com.example.data.repository.PrayerSurahsRepository.allSurahsAndDuas
    assertTrue(items.size >= 15)
    val fatiha = items.find { it.id == "fatiha" }
    assertNotNull(fatiha)
    assertEquals("Fâtiha Suresi", fatiha?.title)
    assertTrue(fatiha?.arabicText?.isNotEmpty() == true)
    assertTrue(fatiha?.turkishPronunciation?.isNotEmpty() == true)

    val subhaneke = items.find { it.id == "subhaneke" }
    assertNotNull(subhaneke)
    assertEquals("Sübhâneke Duâsı", subhaneke?.title)
  }

  @Test
  fun `test 114 surahs catalog loaded`() {
    val surahs = com.example.data.repository.QuranRepository.allSurahs
    assertEquals(114, surahs.size)
    assertEquals(1, surahs.first().number)
    assertEquals("Fâtiha", surahs.first().nameTr)
    assertEquals(114, surahs.last().number)
    assertEquals("Nâs", surahs.last().nameTr)
  }

  @Test
  fun `test Quran offline surahs and ayahs`() = kotlinx.coroutines.runBlocking {
    val fatihaDetail = com.example.data.repository.QuranRepository.getSurahDetail(1)
    assertEquals(7, fatihaDetail.ayahs.size)
    assertTrue(fatihaDetail.ayahs.first().arabicText.contains("بِسْمِ"))
    assertTrue(fatihaDetail.ayahs.first().turkishMeaning.isNotEmpty())

    val ihlasDetail = com.example.data.repository.QuranRepository.getSurahDetail(112)
    assertEquals(4, ihlasDetail.ayahs.size)

    val mulkDetail = com.example.data.repository.QuranRepository.getSurahDetail(67)
    assertTrue(mulkDetail.ayahs.isNotEmpty())

    val nebeDetail = com.example.data.repository.QuranRepository.getSurahDetail(78)
    assertTrue(nebeDetail.ayahs.isNotEmpty())
  }

  @Test
  fun `test mosques and hadiths loaded`() {
    val mosques = com.example.data.repository.MosqueRepository.allMosques
    assertTrue(mosques.size >= 10)
    val ayasofya = mosques.find { it.id == "ist_1" }
    assertNotNull(ayasofya)
    val dist = ayasofya?.calculateDistanceKm(41.0082, 28.9784) ?: 999.0
    assertTrue("Ayasofya should be close to Istanbul center: $dist km", dist < 5.0)

    val hadiths = com.example.data.repository.HadithRepository.allHadiths
    assertTrue(hadiths.size >= 10)
    val niyetHadith = hadiths.find { it.id == "hadith_1" }
    assertNotNull(niyetHadith)
    assertTrue(niyetHadith?.arabicText?.isNotEmpty() == true)
    assertTrue(niyetHadith?.turkishText?.isNotEmpty() == true)
    assertTrue(niyetHadith?.englishText?.isNotEmpty() == true)
  }

  @Test
  fun `test nav destination dualar exists`() {
    val dualar = NavDestination.DUALAR
    assertEquals("dualar", dualar.route)
    assertEquals("Dualar", dualar.getTitle(com.example.data.model.AppLanguage.TURKISH))
    assertEquals("Duas", dualar.getTitle(com.example.data.model.AppLanguage.ENGLISH))
    assertEquals("الأدعية", dualar.getTitle(com.example.data.model.AppLanguage.ARABIC))
  }
}
