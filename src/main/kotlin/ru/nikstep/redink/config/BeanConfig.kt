package ru.nikstep.redink.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import ru.nikstep.redink.repo.RepositoryRepository
import ru.nikstep.redink.repo.SourceCodeRepository
import ru.nikstep.redink.repo.UserRepository
import ru.nikstep.redink.service.*

@Configuration
class BeanConfig {

    @Bean
    fun githubAppService(): GithubAppService {
        return SimpleGithubAppService()
    }

    @Bean
    fun analysisResultService(githubAppService: GithubAppService): AnalysisResultService {
        return AnalysisResultService(githubAppService)
    }

    @Bean
    fun sourceCodeService(
        sourceCodeRepository: SourceCodeRepository,
        userRepository: UserRepository,
        repositoryRepository: RepositoryRepository
    ): SourceCodeService {
        return SourceCodeService(sourceCodeRepository, userRepository, repositoryRepository)
    }

    @Bean
    fun pullRequestService(
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
    fun plagiarismService(
        analysisResultService: AnalysisResultService
    ): PlagiarismService {
        return EmptyPlagiarismService(analysisResultService)
    }

    @Bean
    fun integrationService(
        userRepository: UserRepository,
        repositoryRepository: RepositoryRepository
    ): IntegrationService {
        return IntegrationService(userRepository, repositoryRepository)
    }

}