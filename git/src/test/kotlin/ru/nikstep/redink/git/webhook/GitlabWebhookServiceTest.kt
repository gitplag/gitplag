package ru.nikstep.redink.git.webhook

import io.kotlintest.mock.mock
import ru.nikstep.redink.git.loader.GitlabLoader
import ru.nikstep.redink.model.entity.PullRequest
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.Language
import java.time.LocalDateTime
import java.time.Month

class GitlabWebhookServiceTest : AbstractWebhookServiceTest() {
    override val payload by lazy { readPayloadOf("gitlab") }

    override val gitLoader = mock<GitlabLoader>()

    override val repo = Repository(
        name = "nikita715/plagiarism_test3",
        gitService = GitProperty.GITLAB,
        language = Language.JAVA
    )

    override val webhookService = GitlabWebhookService(pullRequestRepository, repositoryRepository, gitLoader)

    override val pullRequest = PullRequest(
        number = 1,
        creatorName = "nikita715",
        sourceRepoId = 11083523,
        mainRepoId = 1108352312,
        sourceRepoFullName = "nikita715/plagiarism_test3",
        repo = repo,
        headSha = "d647870f53f333e3c0bec84cdd245e7262071331",
        sourceBranchName = "br2",
        mainBranchName = "master",
        date = LocalDateTime.of(2019, Month.FEBRUARY, 28, 22, 12, 32)
    )
}