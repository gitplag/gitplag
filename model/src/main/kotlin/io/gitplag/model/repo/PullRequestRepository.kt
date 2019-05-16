package io.gitplag.model.repo

import io.gitplag.model.entity.PullRequest
import io.gitplag.model.entity.Repository
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Spring data repo of [PullRequest]
 */
interface PullRequestRepository : JpaRepository<PullRequest, Long> {

    /**
     * Find all [PullRequest]s by the [repo] and by the [sourceBranchName]
     */
    fun findAllByRepoAndSourceBranchName(repo: Repository, sourceBranchName: String): List<PullRequest>

    /**
     * Find a [PullRequest] by the [repo] and by the [number]
     */
    fun findByRepoAndNumber(repo: Repository, number: Int): PullRequest?

    /**
     * Find a [PullRequest] by the [repo] and by the [number]
     */
    fun findByRepoAndCreatorNameAndSourceBranchName(
        repo: Repository,
        creatorName: String,
        sourceBranchName: String
    ): PullRequest?

    /**
     * Find all pull requests to repo with the [repoId]
     */
    fun findAllByRepoId(repoId: Long): List<PullRequest>
}