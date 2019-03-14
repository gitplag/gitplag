package ru.nikstep.redink.model.data

import ru.nikstep.redink.model.entity.Repository
import ru.nikstep.redink.util.AnalyserProperty
import ru.nikstep.redink.util.AnalysisBranchMode
import ru.nikstep.redink.util.GitProperty
import ru.nikstep.redink.util.Language

/**
 * Input analysis properties
 */
class AnalysisSettings(
    val repository: Repository,
    val branch: String,
    val branchMode: AnalysisBranchMode = repository.analysisBranchMode,
    val analyser: AnalyserProperty = repository.analyser,
    val gitService: GitProperty = repository.gitService,
    val language: Language = repository.language
)