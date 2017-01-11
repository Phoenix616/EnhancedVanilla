package de.themoep.enhancedvanilla.mechanics;/*
 * Copyright 2017 Max Lee (https://github.com/Phoenix616/)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Mozilla Public License as published by
 * the Mozilla Foundation, version 2.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * Mozilla Public License v2.0 for more details.
 * 
 * You should have received a copy of the Mozilla Public License v2.0
 * along with this program. If not, see <http://mozilla.org/MPL/2.0/>.
 */

import de.themoep.enhancedvanilla.EnhancedVanilla;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;

public abstract class EnhancedMechanic {

    protected final EnhancedVanilla plugin;
    private boolean enabled;
    private String configKey;
    private String permissionNode;
    private String name;

    public EnhancedMechanic(EnhancedVanilla plugin) {
        this.plugin = plugin;
        name = getClass().getSimpleName();

        String[] parts = getName().split("(?<=.)(?=\\p{Lu})");
        StringBuilder sb = new StringBuilder(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            sb.append("-").append(parts[i]);
        }
        configKey = sb.toString().toLowerCase();

        permissionNode = plugin.getName().toLowerCase() + ".mechanics." + getName().toLowerCase();
    }

    public void loadConfig() {
        setEnabled(plugin.getConfig().getBoolean(getConfigKey()));
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        if (this instanceof Listener) {
            if (isEnabled() && !enabled) {
                unregisterListener();
            } else if (!isEnabled() && enabled) {
                plugin.getServer().getPluginManager().registerEvents((Listener) this, plugin);
            }
        }
        this.enabled = enabled;
        plugin.getLogger().log(Level.INFO, getName() + " is " + (enabled ? "enabled" : "disabled"));
    }

    protected void unregisterListener() {
        if (!(this instanceof Listener))
            return;

        Set<Method> methods = new HashSet<>();
        try {
            Collections.addAll(methods, getClass().getMethods());
            Collections.addAll(methods, getClass().getDeclaredMethods());
        } catch (NoClassDefFoundError e) {
            log(Level.WARNING, "Failed to unregister events because " + e.getMessage() + " does not exist.");
        }
        for (Method method : methods) {
            EventHandler eh = method.getAnnotation(EventHandler.class);
            if (eh == null)
                continue;

            if (method.getGenericParameterTypes().length == 1) {
                Class<?> eventClass = method.getParameterTypes()[0];


                try {
                    eventClass.getMethod("getHandlerList");
                } catch (NoSuchMethodException e) {
                    if (eventClass.getSuperclass() != null
                            && !eventClass.getSuperclass().equals(Event.class)
                            && Event.class.isAssignableFrom(eventClass.getSuperclass())) {
                        eventClass = eventClass.getSuperclass();
                    } else {
                        log(Level.WARNING, "Failed to unregister event " + eventClass.getSimpleName() + " of \"" + method.toGenericString() + "\" EventHandler! It doesn't seem to have the getHandlerList method and no super class?");
                        return;
                    }
                }

                if (Event.class.isAssignableFrom(eventClass)) {
                    log(Level.WARNING, "Failed to unregister event due to invalid EventHandler method signature \"" + method.toGenericString() + "\"!");
                    continue;
                }

                try {
                    Method getHandlerList = eventClass.getMethod("getHandlerList");
                    HandlerList handlers = (HandlerList) getHandlerList.invoke(null);
                    handlers.unregister((Listener) this);
                } catch (NoSuchMethodException e) {
                    log(Level.WARNING, "Failed to unregister event " + eventClass.getSimpleName() + " of \"" + method.toGenericString() + "\" EventHandler! It doesn't seem to have the getHandlerList method?");
                } catch (InvocationTargetException e) {
                    log(Level.WARNING, "Failed to unregister event " + eventClass.getSimpleName() + " of \"" + method.toGenericString() + "\" EventHandler! Couldn't invoke getHandlerList method! (" + e.getMessage() + ")");
                } catch (IllegalAccessException e) {
                    log(Level.WARNING, "Failed to unregister event " + eventClass.getSimpleName() + " of \"" + method.toGenericString() + "\" EventHandler! Illegal access on getHandlerList method! (" + e.getMessage() + ")");
                }
            }
        }
    }

    protected void log(Level level, String message) {
        plugin.getLogger().log(level, getName() + " - " + message);
    }

    public String getConfigKey() {
        return configKey;
    }

    public String getPermissionNode() {
        return permissionNode;
    }

    public String getName() {
        return name;
    }
}
