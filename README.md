# Spotless Applier

![Build](https://github.com/lipiridi/spotless-applier/workflows/Build/badge.svg)[gh:build]

<!-- Plugin description -->
An IntelliJ plugin to allow running the [spotless](https://github.com/diffplug/spotless) gradle and maven tasks
from within the IDE on the current file selected in the editor or for the whole project.

Available actions:
* <kbd>Code</kbd> > <kbd>Reformat File With Spotless</kbd> (shortcut <kbd>Ctrl+Alt+;</kbd>)
* <kbd>Code</kbd> > <kbd>Reformat All File With Spotless</kbd> (shortcut <kbd>Ctrl+Alt+Shift;</kbd>)

![spotlessdemo](https://github.com/lipiridi/spotless-applier/assets/60580660/990e7bb9-8b75-4ca4-8973-f1fb2cf74e78)
<!-- Plugin description end -->

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

## Release Notes
See [CHANGELOG.md](CHANGELOG.md)

## Acknowledgments
Thanks to [Ryan Gurney](https://github.com/ragurney) for his [Spotless IntelliJ Gradle](https://github.com/ragurney/spotless-intellij-gradle) plugin 
which I used as the basis for this plugin.

## License
See [License](LICENSE)