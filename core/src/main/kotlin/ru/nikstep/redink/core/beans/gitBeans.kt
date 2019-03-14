package ru.nikstep.redink.core.beans

import org.springframework.context.support.beans
import ru.nikstep.redink.git.integration.GithubIntegrationService
import ru.nikstep.redink.git.webhook.BitbucketWebhookService
import ru.nikstep.redink.git.webhook.GithubWebhookService
import ru.nikstep.redink.git.webhook.GitlabWebhookService

val gitBeans = beans {
    bean<GithubIntegrationService>()
    bean<GitlabWebhookService>()
    bean<BitbucketWebhookService>()
    bean<GithubWebhookService>()
}