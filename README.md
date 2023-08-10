# Spotless Applier

[![Build](https://github.com/lipiridi/spotless-applier/workflows/Build/badge.svg)](https://github.com/lipiridi/spotless-applier/actions)
[![Version](https://img.shields.io/jetbrains/plugin/v/22455.svg)](https://plugins.jetbrains.com/plugin/18321)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/22455.svg)](https://plugins.jetbrains.com/plugin/18321)

<!-- Plugin description -->
The Spotless Applier IntelliJ Plugin enhances your development workflow
by seamlessly integrating [Spotless](https://github.com/diffplug/spotless) Gradle and Maven tasks directly within the IntelliJ IDE.
With this plugin, you can easily apply code formatting and style enforcement to your projects,
either for the current file you're working on or for the entire project.

### Features
* <kbd>Code</kbd> > <kbd>Reformat File With Spotless</kbd> (shortcut <kbd>Ctrl+Alt+;</kbd>)
* <kbd>Code</kbd> > <kbd>Reformat All File With Spotless</kbd> (shortcut <kbd>Ctrl+Alt+Shift+;</kbd>)

### Settings

To access plugin settings, go to: <kbd>Settings</kbd> > <kbd>Tools</kbd> > <kbd>Spotless Applier</kbd>
* <kbd>Optimize imports before applying</kbd> > enable this option to additionally execute IntelliJ IDEA's default "optimize imports" task before applying Spotless formatting.
<!-- Plugin description end -->

### Media

![spotlessdemo](https://github.com/lipiridi/spotless-applier/assets/60580660/990e7bb9-8b75-4ca4-8973-f1fb2cf74e78)
![image](https://github.com/lipiridi/spotless-applier/assets/60580660/ff867657-0e59-46b9-9b90-a7b72ce586bd)

## Install

### Using IDE built-in plugin system:

<kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "Spotless Applier"</kbd> >
<kbd>Install Plugin</kbd>

### Manually:

Download the [latest release](https://github.com/lipiridi/spotless-applier/releases/latest) and install it manually using
<kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

## How it works
This plugin resolve your current build tool by searching the `pom.xml` (Maven) or `.gradle` (Gradle) files in project's root folder.
After that, it runs the `spotless:apply` or `spotlessApply` task.

For applying changes only for current file it uses:
* Maven [Specific files](https://github.com/diffplug/spotless/tree/main/plugin-maven#can-i-apply-spotless-to-specific-files)
* Gradle [Spotless IDE hook](https://github.com/diffplug/spotless/blob/main/plugin-gradle/IDE_HOOK.md)

## Acknowledgments
Thanks to [Ryan Gurney](https://github.com/ragurney) for his [Spotless IntelliJ Gradle](https://github.com/ragurney/spotless-intellij-gradle) plugin
which I used as the basis for this plugin.

## Release Notes
See [CHANGELOG.md](CHANGELOG.md)

## License
See [License](LICENSE)