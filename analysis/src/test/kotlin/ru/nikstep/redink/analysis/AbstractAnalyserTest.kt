package ru.nikstep.redink.analysis

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import ru.nikstep.redink.model.data.AnalysisSettings
import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.model.enums.AnalysisMode
import ru.nikstep.redink.model.enums.GitProperty
import ru.nikstep.redink.model.enums.Language
import ru.nikstep.redink.util.RandomGenerator
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Abstract test for the plagiarism analysers
 */
abstract class AbstractAnalyserTest {
    protected val testRepoName = "nikita715/plagiarism_test"

    protected val student1 = "student1"
    protected val student2 = "student2"
    protected val student3 = "student3"
    protected val file1Name = "file1.java"
    protected val file2Name = "file2.java"
    protected val file3Name = "file3.java"
    protected val file4Name = "file4.java"
    protected val file5Name = "file5.java"
    protected val file6Name = "file6.java"
    protected val file7Name = "file7.java"
    protected val file8Name = "file8.java"
    protected val file9Name = "file9.java"

    protected val sha1 = "sha1"
    protected val sha2 = "sha2"
    protected val sha3 = "sha3"

    protected val hash = "hash"

    protected val createdAtList = (0L..2L).map { LocalDateTime.ofEpochSecond(it, 0, ZoneOffset.UTC) }

    protected val randomGenerator = mock<RandomGenerator> {
        on { randomAlphanumeric(10) } doReturn hash
    }

    private val repository = mock<Repository> {
        on { gitService } doReturn GitProperty.GITHUB
        on { name } doReturn testRepoName
    }

    protected val analysisSettings = mock<AnalysisSettings> {
        on { repository } doReturn repository
        on { language } doReturn Language.JAVA
        on { branch } doReturn "master"
        on { mode } doReturn AnalysisMode.FULL
    }
}