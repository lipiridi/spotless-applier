package com.github.lipiridi.spotless.applier.startup

import com.github.lipiridi.spotless.applier.onSave.SpotlessOnSaveOptions
import com.intellij.openapi.application.readAction
import com.intellij.openapi.project.Project
import com.intellij.openapi.startup.ProjectActivity

/**
 * Forces the [SpotlessOnSaveOptions] project light service to load off the EDT during project
 * startup. Without this, the first `getInstance(project)` call happens on the EDT inside
 * `ActionsOnSaveFileDocumentManagerListener.beforeAllDocumentsSaving` (i.e. on every save), which
 * triggers the persisted-state load. In a Maven project IntelliJ then expands path macros via
 * `MavenProjectPathMacroContributor`, which calls `EelProvider.toEelApiBlocking` — forbidden on the
 * EDT and fatal in ijent-based Dev Container mode.
 */
internal class SpotlessApplierStartupActivity : ProjectActivity {
    override suspend fun execute(project: Project) {
        readAction {
            SpotlessOnSaveOptions.getInstance(project)
        }
    }
}
