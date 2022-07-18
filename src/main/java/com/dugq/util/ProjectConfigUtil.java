package com.dugq.util;

import com.intellij.openapi.module.Module;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

/**
 * @author dugq
 * @date 2021/8/16 12:03 上午
 */
public class ProjectConfigUtil {

    public static GlobalSearchScope getModuleScope(@NotNull Module module) {
        return getModuleScope(module,false);
    }

    protected static GlobalSearchScope getModuleScope(@NotNull Module module, boolean hasLibrary) {
        if (hasLibrary) {
            return module.getModuleWithLibrariesScope();
        } else {
            return module.getModuleScope();
        }
    }

}
