package io.gitplag.core.webhook

import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import io.gitplag.git.payload.GitManager
import org.junit.Before
import org.junit.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.setup.MockMvcBuilders

abstract class AbstractWebhookControllerTest {

    abstract val gitManager: GitManager
    abstract val webhookController: Any

    abstract val endpoint: String
    abstract val headerName: String
    abstract val pushEvent: String
    abstract val prEvent: String

    private lateinit var mockMvc: MockMvc

    private val payload = "payload"

    @Before
    fun setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(webhookController).build()
    }

    @Test
    fun processPullRequest() {
        mockMvc.perform(
            MockMvcRequestBuilders.post(endpoint).header(headerName, prEvent).content(payload)
                .contentType(MediaType.APPLICATION_JSON)
        )
        verify(gitManager).downloadSolutionsOfPullRequest(payload)
    }

    @Test
    fun processPush() {
        mockMvc.perform(
            MockMvcRequestBuilders.post(endpoint).header(headerName, pushEvent).content(payload)
                .contentType(MediaType.APPLICATION_JSON)
        )
        verify(gitManager).downloadBasesOfRepository(payload)
    }

    @Test
    fun processNothing() {
        mockMvc.perform(
            MockMvcRequestBuilders.post(endpoint).content(payload)
                .contentType(MediaType.APPLICATION_JSON)
        )
        verify(gitManager, never()).downloadBasesOfRepository(payload)
        verify(gitManager, never()).downloadSolutionsOfPullRequest(payload)
    }
}