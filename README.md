# Spotless Applier

[![Build](https://github.com/lipiridi/spotless-applier/workflows/Build/badge.svg)](https://github.com/lipiridi/spotless-applier/actions)
[![Version](https://img.shields.io/jetbrains/plugin/v/22455.svg)](https://plugins.jetbrains.com/plugin/22455)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/22455.svg)](https://plugins.jetbrains.com/plugin/22455)

<!-- Plugin description -->
The Spotless Applier IntelliJ Plugin enhances your development workflow
by seamlessly integrating [Spotless](https://github.com/diffplug/spotless) Gradle and Maven tasks directly within the IntelliJ IDE.
With this plugin, you can easily apply code formatting and style enforcement to your projects,
either for the current file you're working on or for the entire project.

### Features
| Actions                                                       | Shortcuts (Windows)                                          | Shortcuts (MacOS)                                              |
|---------------------------------------------------------------|--------------------------------------------------------------|----------------------------------------------------------------|
| <kbd>Code</kbd> > <kbd>Reformat File With Spotless</kbd>      | <kbd>Ctrl</kbd>+<kbd>Alt</kbd>+<kbd>;</kbd>                  | <kbd>⌘Сmd</kbd>+<kbd>⌥Opt</kbd>+<kbd>;</kbd>                   |
| <kbd>Code</kbd> > <kbd>Reformat All Files With Spotless</kbd> | <kbd>Ctrl</kbd>+<kbd>Alt</kbd>+<kbd>Shift</kbd>+<kbd>;</kbd> | <kbd>⌘Сmd</kbd>+<kbd>⌥Opt</kbd>+<kbd>⇧Shift</kbd>+<kbd>;</kbd> |

### Settings

To access plugin settings, go to: `Settings` > `Tools` > `Spotless Applier`
* `Allow Gradle cache for 'apply' task` > Starting from version 6.0.0, Spotless supports Gradle's configuration cache. If you want to use it, please enable this checkbox.
* `Optimize imports before applying` > Enable this option to additionally execute IntelliJ IDEA's default "optimize imports" task before applying Spotless formatting.
  * `Prohibit imports with asterisk '*'` > Prevents imports with the asterisk symbol during the "optimize imports" task. When enabled, any imports that use the '*' symbol to import all classes from a package will be replaced with FQN.
<!-- Plugin description end -->

### Media

![spotlessdemo](https://github.com/lipiridi/spotless-applier/assets/60580660/990e7bb9-8b75-4ca4-8973-f1fb2cf74e78)
![image](https://github.com/lipiridi/spotless-applier/assets/60580660/0d6ba567-e955-4193-b85b-f06e0541c790)


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

## Contributing
See [CONTRIBUTING.md](CONTRIBUTING.md)

## Release Notes
See [CHANGELOG.md](CHANGELOG.md)

## License
See [License](LICENSE)