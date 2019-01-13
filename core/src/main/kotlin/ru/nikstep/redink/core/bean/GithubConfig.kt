package ru.nikstep.redink.core.bean

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.nikstep.redink.github.service.AnalysisResultService
import ru.nikstep.redink.github.service.EmptyPlagiarismService
import ru.nikstep.redink.github.service.GithubAppService
import ru.nikstep.redink.github.service.IntegrationService
import ru.nikstep.redink.github.service.PlagiarismService
import ru.nikstep.redink.github.service.PullRequestWebhookService
import ru.nikstep.redink.github.service.SimpleGithubAppService
import ru.nikstep.redink.github.service.SourceCodeService
import ru.nikstep.redink.model.repo.RepositoryRepository
import ru.nikstep.redink.model.repo.SourceCodeRepository
import ru.nikstep.redink.model.repo.UserRepository

@Configuration
open class GithubConfig {

    @Bean
    open fun githubAppService(): GithubAppService {
        return SimpleGithubAppService()
    }

    @Bean
    open fun analysisResultService(githubAppService: GithubAppService): AnalysisResultService {
        return AnalysisResultService(githubAppService)
    }

    @Bean
    open fun sourceCodeService(
        sourceCodeRepository: SourceCodeRepository,
        userRepository: UserRepository,
        repositoryRepository: RepositoryRepository
    ): SourceCodeService {
        return SourceCodeService(sourceCodeRepository, userRepository, repositoryRepository)
    }

    @Bean
    open fun pullRequestService(
        repositoryRepository: RepositoryRepository,
        sourceCodeService: SourceCodeService,
        githubAppService: GithubAppService,
        plagiarismService: PlagiarismService,
        analysisResultService: AnalysisResultService
    ): PullRequestWebhookService {
        return PullRequestWebhookService(
            repositoryRepository,
            sourceCodeService,
            githubAppService,
            plagiarismService,
            analysisResultService
        )
    }

    @Bean
    open fun plagiarismService(
        analysisResultService: AnalysisResultService
    ): PlagiarismService {
        return EmptyPlagiarismService(analysisResultService)
    }

    @Bean
    open fun integrationService(
        userRepository: UserRepository,
        repositoryRepository: RepositoryRepository
    ): IntegrationService {
        return IntegrationService(userRepository, repositoryRepository)
    }

}