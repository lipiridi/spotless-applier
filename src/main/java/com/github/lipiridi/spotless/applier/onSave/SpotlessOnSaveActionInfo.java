// Copyright 2000-2023 JetBrains s.r.o. and contributors. Use of this source code is governed by the
// Apache 2.0 license.
package com.github.lipiridi.spotless.applier.onSave;

import com.intellij.ide.actionsOnSave.ActionOnSaveContext;
import com.intellij.lang.Language;
import com.intellij.lang.LanguageFormatting;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.util.Key;
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider;
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.containers.MultiMap;
import java.util.Collection;
import java.util.function.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SpotlessOnSaveActionInfo extends SpotlessOnSaveActionInfoBase<SpotlessOnSaveOptions> {

    private static final Key<SpotlessOnSaveOptions> CURRENT_UI_STATE_KEY =
            Key.create("spotless.format.on.save.options");

    public SpotlessOnSaveActionInfo(@NotNull ActionOnSaveContext context) {
        super(context, "Run spotless", CURRENT_UI_STATE_KEY);
    }

    @Override
    protected @NotNull SpotlessOnSaveOptions getOptionsFromStoredState() {
        return SpotlessOnSaveOptions.getInstance(getProject());
    }

    @Override
    protected void addApplicableFileTypes(@NotNull Collection<? super FileType> result) {
        // The formatting capability of a file is generally determined by its programming language
        // rather than its file type.
        // The UI task involves displaying all file types that
        //  - have the defined file name pattern (fileTypeManager.getAssociations())
        //  - can be formatted
        // We perform various language-based checks to assess the code's formattability.
        // Upon successful verification, we add all associated file types for that language to the UI
        // list.

        // Another proposed solution involves displaying only the file types associated
        // with each language aka `language.getAssociatedFileType()`.
        // However, this approach was rejected (lost significant JS dialects),
        // and the details can be found in the file history (CPP-37117).

        // prepare the language to "file types with patterns" map
        MultiMap<Language, LanguageFileType> languageFileTypes = new MultiMap<>();
        FileTypeManager fileTypeManager = FileTypeManager.getInstance();
        for (FileType fileType : fileTypeManager.getRegisteredFileTypes()) {
            if (fileType instanceof LanguageFileType lft
                    && !fileTypeManager.getAssociations(fileType).isEmpty()) {
                languageFileTypes.putValue(lft.getLanguage(), lft);
            }
        }

        // if the language is formattable, add all file types from the created map
        Consumer<Language> addLanguageFileTypes = (@Nullable Language language) -> {
            if (language != null) {
                result.addAll(languageFileTypes.get(language));
                ContainerUtil.addIfNotNull(result, language.getAssociatedFileType());
            }
        };

        // add all file types that can be handled by the IDE internal formatter (== have
        // FormattingModelBuilder)
        for (Language language : languageFileTypes.keySet()) {
            if (LanguageFormatting.INSTANCE.forLanguage(language) != null) {
                addLanguageFileTypes.accept(language);
            }
        }

        // Iterating only FormattingModelBuilders is not enough. Some file types may get formatted by
        // external formatter integrated in the IDE (like ShExternalFormatter).
        //
        // A good sign that IDE supports some file type formatting is that it has a Code Style page for
        // this file type. The following code makes
        // sure that all file types that have their Code Style pages are included in the result set.
        //
        // The logic of iterating Code Style pages is similar to what's done in
        // CodeStyleSchemesConfigurable.buildConfigurables()
        for (CodeStyleSettingsProvider provider : CodeStyleSettingsProvider.EXTENSION_POINT_NAME.getExtensionList()) {
            if (provider.hasSettingsPage()) {
                addLanguageFileTypes.accept(provider.getLanguage());
            }
        }
        for (LanguageCodeStyleSettingsProvider provider :
                LanguageCodeStyleSettingsProvider.getSettingsPagesProviders()) {
            addLanguageFileTypes.accept(provider.getLanguage());
        }
    }

    @Override
    protected void apply() {
        getOptionsFromStoredState().loadState(getCurrentUiState().getState().clone());
    }
}
