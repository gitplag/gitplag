package ru.nikstep.redink.analysis.analyser

import mu.KotlinLogging
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import ru.nikstep.redink.analysis.solutions.SolutionStorage
import ru.nikstep.redink.model.data.AnalysisMatch
import ru.nikstep.redink.model.data.AnalysisResult
import ru.nikstep.redink.model.data.AnalysisSettings
import ru.nikstep.redink.model.data.MatchedLines
import ru.nikstep.redink.model.data.Solution
import ru.nikstep.redink.model.data.findByStudent
import java.time.LocalDateTime

/**
 * Moss client wrapper
 */
class MossAnalyser(
    private val solutionStorage: SolutionStorage,
    private val mossId: String
) : Analyser {
    private val logger = KotlinLogging.logger {}

    override fun analyse(analysisSettings: AnalysisSettings): AnalysisResult {
        val analysisFiles = solutionStorage.loadBasesAndComposedSolutions(analysisSettings)
        val resultLink = MossClient(analysisFiles, mossId).run()
        val matchData = parseResult(analysisSettings, analysisFiles.solutions, resultLink)
        val executionDate = LocalDateTime.now()
        return AnalysisResult(analysisSettings, resultLink, executionDate, matchData)
    }

    private fun parseResult(
        analysisSettings: AnalysisSettings,
        solutions: List<Solution>,
        resultLink: String
    ): List<AnalysisMatch> {
        return Jsoup.connect(resultLink).get()
            .body()
            .getElementsByTag("table")
            .select("tr")
            .drop(1)
            .map { tr -> tr.select("td") }
            .mapNotNull { tds ->
                val firstATag = tds[0].selectFirst("a")
                val secondATag = tds[1].selectFirst("a")

                val firstPath = firstATag.text().split(" ").first().split("/")
                val secondPath = secondATag.text().split(" ").first().split("/")

                val students =
                    firstPath.zip(secondPath)
                        .dropWhile { it.first == it.second }
                        .first()

                val lines = tds[2].text().toInt()

                val percentage = firstATag.text().split(" ")
                    .last()
                    .removeSurrounding("(", "%)")
                    .toInt()

                val matchedLines =
                    if (analysisSettings.withLines)
                        findMatchedLines(firstATag, solutions, students)
                    else listOf()

                AnalysisMatch(
                    students = students.first to students.second,
                    lines = lines,
                    percentage = percentage,
                    matchedLines = matchedLines,
                    sha = findByStudent(solutions, students.first).sha
                            to findByStudent(solutions, students.second).sha
                )
            }
    }

    private fun findMatchedLines(
        a: Element,
        solutions: List<Solution>,
        students: Pair<String, String>
    ): List<MatchedLines> {
        val allMatchedRows = Jsoup.connect(a.attr("href").replace(".html", "-top.html"))
            .get().getElementsByTag("tr")
        val leftMatchedLines = mutableListOf<Pair<Int, Int>>()
        val rightMatchedLines = mutableListOf<Pair<Int, Int>>()
        for (row in allMatchedRows.subList(1, allMatchedRows.size)) {
            val cells = row.getElementsByTag("td")
            val firstMatch = cells[0].selectFirst("a").text().split("-")
            val secondMatch = cells[2].selectFirst("a").text().split("-")
            leftMatchedLines += firstMatch[0].toInt() to firstMatch[1].toInt()
            rightMatchedLines += secondMatch[0].toInt() to secondMatch[1].toInt()
        }

        val solution1 = findByStudent(solutions, students.first)
        val solution2 = findByStudent(solutions, students.second)

        val leftFilesToMatchedLines = filesToMatchedLines(leftMatchedLines, solution1)
        val rightFilesToMatchedLines = filesToMatchedLines(rightMatchedLines, solution2)

        return (0 until leftFilesToMatchedLines.size).map { i ->
            MatchedLines(
                match1 = leftFilesToMatchedLines[i].second.first to leftFilesToMatchedLines[i].second.second,
                match2 = rightFilesToMatchedLines[i].second.first to rightFilesToMatchedLines[i].second.second,
                files = leftFilesToMatchedLines[i].first to rightFilesToMatchedLines[i].first
            )
        }
    }

    private fun filesToMatchedLines(matchedLines: List<Pair<Int, Int>>, solution: Solution) =
        matchedLines.map {
            var index = 0
            for (i in solution.lengths) {
                if (it.first >= i) {
                    index++
                }
            }
            if (index > 0)
                solution.files[index] to
                        (it.first - solution.lengths[index - 1]
                                to it.second - solution.lengths[index - 1]) else
                solution.files[index] to it
        }

}