package com.tutuur.navigator.plugin

import com.android.build.gradle.AppPlugin
import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

class NavigatorPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {

        def appPlugin = project.plugins.withType(AppPlugin)

        if (!appPlugin) {
            throw new IllegalStateException("apply navigator plugin to android application only.")
        }

        // create script configuration block.
        def extension = project.extensions.create("navigator", NavigatorPluginExtension, project)

        project.extensions
                .findByType(BaseExtension)
                .registerTransform(new NavigatorTransform())
    }
}

