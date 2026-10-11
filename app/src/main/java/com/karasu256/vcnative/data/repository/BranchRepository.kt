package com.karasu256.vcnative.data.repository

import com.karasu256.vcnative.data.model.Branch
import com.karasu256.vcnative.data.model.BranchCommit
import com.karasu256.vcnative.data.model.Project
import com.karasu256.vcnative.data.remote.GitHubApi
import com.karasu256.vcnative.data.remote.VercelApi
import kotlinx.coroutines.CancellationException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BranchRepository @Inject constructor(
    private val apiKeyRepository: ApiKeyRepository,
    private val gitHubAuthRepository: GitHubAuthRepository,
    private val api: VercelApi,
    private val gitHubApi: GitHubApi,
) {
    suspend fun getBranches(project: Project): List<Branch> =
        getBranchesFromGitHub(project) ?: getBranchesFromDeployments(project)

    suspend fun getCommit(project: Project, branch: Branch): BranchCommit? {
        branch.commit?.let { return it }
        val token = gitHubAuthRepository.read() ?: return null
        val org = project.org ?: return null
        val repo = project.repo ?: return null
        return try {
            val commit = gitHubApi.getCommit(token, org, repo, branch.name)
            BranchCommit(
                message = commit.commit.message,
                sha = commit.sha,
                author = commit.author?.login ?: commit.commit.author?.name,
                committedAt = commit.commit.author?.date?.let(::parseIsoDate),
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getBranchesFromGitHub(project: Project): List<Branch>? {
        val token = gitHubAuthRepository.read() ?: return null
        val org = project.org ?: return null
        val repo = project.repo ?: return null
        return try {
            gitHubApi.getBranches(token, org, repo).map { branch ->
                Branch(name = branch.name, sha = branch.commit.sha, commit = null)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun getBranchesFromDeployments(project: Project): List<Branch> {
        val apiKey = apiKeyRepository.read() ?: return emptyList()
        val deployments = try {
            api.getProjectDeployments(apiKey, project.id)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            return emptyList()
        }
        return deployments
            .filter { deployment ->
                (project.org == null || deployment.meta?.githubOrg == project.org) &&
                    (project.repo == null || deployment.meta?.githubRepo == project.repo)
            }
            .sortedByDescending { it.created }
            .mapNotNull { deployment ->
                val meta = deployment.meta ?: return@mapNotNull null
                val name = meta.githubCommitRef ?: return@mapNotNull null
                Branch(
                    name = name,
                    sha = meta.githubCommitSha,
                    commit = BranchCommit(
                        message = meta.githubCommitMessage,
                        sha = meta.githubCommitSha,
                        author = meta.githubCommitAuthorName,
                        committedAt = deployment.created,
                    ),
                )
            }
            .distinctBy { it.name }
    }

    private fun parseIsoDate(value: String): Long? = runCatching {
        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            .apply { timeZone = TimeZone.getTimeZone("UTC") }
            .parse(value)
            ?.time
    }.getOrNull()
}
