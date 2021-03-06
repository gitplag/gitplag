package io.gitplag.git.agent

import io.gitplag.analysis.solutions.SourceCodeStorage
import io.gitplag.model.entity.PullRequest
import io.gitplag.model.entity.Repository
import io.gitplag.model.repo.SolutionFileRecordRepository
import io.gitplag.util.downloadAndUnpackZip
import mu.KotlinLogging
import java.io.File

/**
 * Common implementation of the [GitAgent]
 */
abstract class AbstractGitAgent(
    private val sourceCodeStorage: SourceCodeStorage,
    private val solutionFileRecordRepository: SolutionFileRecordRepository,
    private val accessToken: String
) : GitAgent {

    private val logger = KotlinLogging.logger {}

    override fun clonePullRequest(pullRequest: PullRequest) {
        val resourceUrl = linkToRepoArchive(pullRequest.sourceRepoFullName, pullRequest.sourceBranchName)
        logger.info { "Git: trying to download the zip from url $resourceUrl" }
        downloadAndUnpackZip(resourceUrl, accessToken) { unpackedDir ->
            val sourceDir = File(unpackedDir).listFiles()[0].absolutePath
            sourceCodeStorage.saveSolutionsFromDir(
                sourceDir, pullRequest
            )
        }
        logger.info { "Git: downloaded and unpacked the zip from url $resourceUrl" }
    }

    override fun deletePullRequestFiles(pullRequest: PullRequest) {
        solutionFileRecordRepository.findAllByPullRequest(pullRequest).forEach { file ->
            sourceCodeStorage.deleteSolutionFile(
                pullRequest.repo,
                pullRequest.sourceBranchName,
                pullRequest.creatorName,
                file.fileName
            )
        }
    }

    override fun cloneRepository(repo: Repository, branch: String?) {
        if (branch == null) {
            findBranchesOfRepo(repo).forEach { cloneBranchOfRepository(repo, it) }
        } else {
            cloneBranchOfRepository(repo, branch)
        }
    }

    protected abstract fun linkToRepoArchive(repoName: String, branchName: String): String

    private fun cloneBranchOfRepository(repo: Repository, branch: String) {
        logger.info { "Git: download zip archive of repo = ${repo.name}, branch = $branch" }
        downloadAndUnpackZip(linkToRepoArchive(repo.name, branch), accessToken) { unpackedDir ->
            val sourceDir = File(unpackedDir).listFiles()[0].absolutePath
            sourceCodeStorage.saveBasesFromDir(
                sourceDir,
                repo, branch
            )
        }
    }
}