package org.bodik;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseMotionListener;

public class GlobalInputListener implements NativeKeyListener, NativeMouseListener, NativeMouseMotionListener {

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeEvent) {
        AntiAFKApp.resetInactivityTimer();
    }

    @Override
    public void nativeMouseClicked(NativeMouseEvent nativeEvent) {
        AntiAFKApp.resetInactivityTimer();
    }

    @Override
    public void nativeMouseMoved(NativeMouseEvent nativeEvent) {
        AntiAFKApp.resetInactivityTimer();
    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent nativeEvent) {
        AntiAFKApp.resetInactivityTimer();
    }

}
