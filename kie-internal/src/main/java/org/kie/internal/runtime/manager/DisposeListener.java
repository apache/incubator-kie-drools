package org.kie.internal.runtime.manager;

import org.kie.api.runtime.manager.RuntimeEngine;

/**
 * Callback listener interface to receive notification when <code>Disposable</code>
 * instances are actually disposed.
 *
 */
public interface DisposeListener {

    /**
     * Invoked by instances that are going to be disposed.
     * @param runtime RuntimeEngine instance that is going to be disposed.
     */
    void onDispose(RuntimeEngine runtime);
}
