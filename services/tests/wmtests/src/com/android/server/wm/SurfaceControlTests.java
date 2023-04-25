/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.wm;

import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Parcel;
import android.platform.test.annotations.Presubmit;
import android.util.Log;
import android.view.SurfaceControl;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.test.filters.SmallTest;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class for testing {@link SurfaceControl}.
 *
 * Build/Install/Run:
 *  atest WmTests:SurfaceControlTests
 */
@Presubmit
@RunWith(AndroidJUnit4.class)
public class SurfaceControlTests {

    @SmallTest
    @Test
    public void testUseValidSurface() {
        SurfaceControl sc = buildTestSurface();
        SurfaceControl.Transaction t = new SurfaceControl.Transaction();
        t.setVisibility(sc, false);
        sc.release();
    }

    @SmallTest
    @Test
    public void testUseInvalidSurface() {
        SurfaceControl sc = buildTestSurface();
        SurfaceControl.Transaction t = new SurfaceControl.Transaction();
        sc.release();
        try {
            t.setVisibility(sc, false);
            fail("Expected exception from updating invalid surface");
        } catch (Exception e) {
            // Expected exception
        }
    }

    @SmallTest
    @Test
    public void testUseInvalidSurface_debugEnabled() {
        SurfaceControl sc = buildTestSurface();
        SurfaceControl.Transaction t = new SurfaceControl.Transaction();
        try {
            SurfaceControl.setDebugUsageAfterRelease(true);
            sc.release();
            try {
                t.setVisibility(sc, false);
                fail("Expected exception from updating invalid surface");
            } catch (IllegalStateException ise) {
                assertNotNull(ise.getCause());
            } catch (Exception e) {
                fail("Expected IllegalStateException with cause");
            }
        } finally {
            SurfaceControl.setDebugUsageAfterRelease(false);
        }
    }

    @SmallTest
    @Test
    public void testWriteInvalidSurface_debugEnabled() {
        SurfaceControl sc = buildTestSurface();
        SurfaceControl.Transaction t = new SurfaceControl.Transaction();
        Parcel p = Parcel.obtain();
        try {
            SurfaceControl.setDebugUsageAfterRelease(true);
            sc.release();
            try {
                sc.writeToParcel(p, 0 /* flags */);
                fail("Expected exception from writing invalid surface to parcel");
            } catch (IllegalStateException ise) {
                assertNotNull(ise.getCause());
            } catch (Exception e) {
                fail("Expected IllegalStateException with cause");
            }
        } finally {
            SurfaceControl.setDebugUsageAfterRelease(false);
            p.recycle();
        }
    }

    @Test
    public void testSurfaceChangedOnRotation() {
        final Instrumentation instrumentation = getInstrumentation();
        final Context context = instrumentation.getContext();
        final Activity activity = instrumentation.startActivitySync(new Intent().setComponent(
                new ComponentName(context, ActivityOptionsTest.MainActivity.class))
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        final SurfaceView sv = new SurfaceView(activity);
        final AtomicInteger surfaceChangedCount = new AtomicInteger();
        instrumentation.runOnMainSync(() -> activity.setContentView(sv));
        sv.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
            }
            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width,
                    int height) {
                surfaceChangedCount.getAndIncrement();
                Log.i("surfaceChanged", "width=" + width + " height=" + height
                        + " getTransformHint="
                        + sv.getViewRootImpl().getSurfaceControl().getTransformHint());
            }
            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
            }
        });
        final int rotation = activity.getResources().getConfiguration()
                .windowConfiguration.getRotation();
        activity.setRequestedOrientation(activity.getResources().getConfiguration().orientation
                == Configuration.ORIENTATION_PORTRAIT
                ? ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                : ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        instrumentation.getUiAutomation().syncInputTransactions();
        instrumentation.waitForIdleSync();
        final int newRotation = activity.getResources().getConfiguration()
                .windowConfiguration.getRotation();
        final int count = surfaceChangedCount.get();
        activity.finishAndRemoveTask();
        // The first count is triggered from creation, so the target number is 2.
        if (rotation != newRotation && count > 2) {
            fail("More than once surfaceChanged for rotation change: " + count);
        }
    }

    private SurfaceControl buildTestSurface() {
        return new SurfaceControl.Builder()
                .setContainerLayer()
                .setName("SurfaceControlTests")
                .setCallsite("SurfaceControlTests")
                .build();
    }
}
