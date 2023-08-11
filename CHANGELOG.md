<!-- Keep a Changelog guide -> https://keepachangelog.com -->

# Spotless Applier Plugin Changelog

## [Unreleased]
### Added
- Option that prohibit imports with asterisk '*' during "optimize imports" task. It temporarily replaces import settings

## [1.0.0]
### Added
- Support for applying Spotless formatting to both Gradle and Maven based projects.
- The ability to apply Spotless formatting to the currently opened file in the editor or to the entire project.
- Introduced an option to execute the default "optimize import" task before applying Spotless formatting.