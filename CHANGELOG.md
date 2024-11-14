<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Spotless Applier Plugin Changelog

## [Unreleased]

## [1.1.4] - 2024-11-14

### Changed

- [#32](https://github.com/lipiridi/spotless-applier/issues/32) Update version compatibility
- [#32](https://github.com/lipiridi/spotless-applier/issues/32) Store pre-commit state in workspace

## [1.1.3] - 2024-10-10

### Changed

- [#27](https://github.com/lipiridi/spotless-applier/issues/27) Run spotless using root config if plugin is not
  applied to the submodule

## [1.1.2] - 2024-08-13

### Changed

- Gradle task for the entire project is again called in background (async)
- Pre-commit hook added in version 1.0.5 is no more supported for gradle projects

## [1.1.1] - 2024-08-11

### Changed

- Fix commit checkin handler for multi-module projects
- isAllowedGradleCache replaced with isProhibitGradleCache, as by default we don't have to pass any args to the gradle command

## [1.1.0] - 2024-06-02

### Added

- 'Run spotless' action on save

## [1.0.5] - 2024-04-14

### Added

- Reformat code with spotless commit checkin handler

### Changed

- Run gradle task for the entire project in sync mode
- Increase Idea compatibility version (sinceBuild) to 241

## [1.0.4] - 2023-12-11

### Changed

- Update Gradle version
- Remove untilBuild property and make plugin compatible with all future intellij version

## [1.0.3] - 2023-08-17

### Changed

- Fix UI settings (apply button worked incorrectly)

## [1.0.2] - 2023-08-15

### Added

- Support for multi-module projects
- Option that allow Gradle's configuration cache for 'apply' task

### Changed

- Rename task "Reformat All Files With Spotless" to "Reformat Project With Spotless"

## [1.0.1] - 2023-08-11

### Added

- Option that prohibit imports with asterisk '*' during "optimize imports" task. It temporarily replaces import settings

## [1.0.0] - 2023-08-10

### Added

- Support for applying Spotless formatting to both Gradle and Maven based projects.
- The ability to apply Spotless formatting to the currently opened file in the editor or to the entire project.
- Introduced an option to execute the default "optimize import" task before applying Spotless formatting.

[Unreleased]: https://github.com/lipiridi/spotless-applier/compare/v1.1.4...HEAD
[1.1.4]: https://github.com/lipiridi/spotless-applier/compare/v1.1.3...v1.1.4
[1.1.3]: https://github.com/lipiridi/spotless-applier/compare/v1.1.2...v1.1.3
[1.1.2]: https://github.com/lipiridi/spotless-applier/compare/v1.1.1...v1.1.2
[1.1.1]: https://github.com/lipiridi/spotless-applier/compare/v1.1.0...v1.1.1
[1.1.0]: https://github.com/lipiridi/spotless-applier/compare/v1.0.5...v1.1.0
[1.0.5]: https://github.com/lipiridi/spotless-applier/compare/v1.0.4...v1.0.5
[1.0.4]: https://github.com/lipiridi/spotless-applier/compare/v1.0.3...v1.0.4
[1.0.3]: https://github.com/lipiridi/spotless-applier/compare/v1.0.2...v1.0.3
[1.0.2]: https://github.com/lipiridi/spotless-applier/compare/v1.0.1...v1.0.2
[1.0.1]: https://github.com/lipiridi/spotless-applier/compare/v1.0.0...v1.0.1
[1.0.0]: https://github.com/lipiridi/spotless-applier/commits/v1.0.0
