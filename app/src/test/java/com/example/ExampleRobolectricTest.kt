package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.DownloadTask
import com.example.data.model.ExecutionState
import com.example.engine.ExtractorEngine
import com.example.engine.PlatformDetector
import kotlinx.coroutines.test.runTest
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
  fun `read app name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Rehman", appName)
  }

  @Test
  fun `verify platform detector for user movie sites and social platforms`() {
    val worldfree = PlatformDetector.detect("https://worldfree4u.expert/movie/sample")
    assertEquals("worldfree4u", worldfree.platformKey)

    val bollyzilla = PlatformDetector.detect("https://bollyzilla.xyz/download/test")
    assertEquals("bollyzilla", bollyzilla.platformKey)

    val moviesspace = PlatformDetector.detect("https://www.moviesspace.net/browse/category/punjabi")
    assertEquals("moviesspace", moviesspace.platformKey)

    val filmvup = PlatformDetector.detect("https://filmvup.com/series/test")
    assertEquals("filmvup", filmvup.platformKey)

    val watchmovies = PlatformDetector.detect("https://www.watch-movies.com.pk/category/free-indian-movies-watch/2025-movies/")
    assertEquals("watchmovies", watchmovies.platformKey)

    val youtube = PlatformDetector.detect("https://www.youtube.com/watch?v=dQw4w9WgXcQ")
    assertEquals("youtube", youtube.platformKey)
    assertTrue(youtube.availableQualities.isNotEmpty())
  }

  @Test
  fun `verify download task progress percent`() {
    val task = DownloadTask(
      id = "test-1",
      currentDownloadUrl = "https://cdn.example.com/stream.mp4?token=123",
      originalSourcePageUrl = "https://example.com/video/1",
      executionState = ExecutionState.DOWNLOADING,
      downloadedBytes = 50_000_000L,
      totalBytes = 100_000_000L,
      fileMimeType = "video/mp4"
    )

    assertEquals(0.5f, task.progressPercent, 0.001f)
    assertEquals(false, task.isExpired)
  }

  @Test
  fun `verify extractor renewal flow`() = runTest {
    val extractor = ExtractorEngine()
    val result = extractor.renewStreamLink(
      originalSourcePageUrl = "https://youtube.com/watch?v=sample",
      currentDownloadUrl = "https://cdn.host.stream/video.mp4?token=old_token"
    )
    assertNotNull(result)
  }
}
