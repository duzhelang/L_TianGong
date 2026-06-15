package com.ecocarbon.mrv.engine.methodology;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class MethodologyPluginManager {
    private final Map<String, MethodologyPlugin> plugins = new ConcurrentHashMap<>();

    public void registerPlugin(MethodologyPlugin plugin) {
        plugins.put(plugin.getCode(), plugin);
    }

    public MethodologyPlugin getPlugin(String code) {
        return plugins.get(code);
    }

    public List<MethodologyPlugin> getAllPlugins() {
        return List.copyOf(plugins.values());
    }

    public List<MethodologyPlugin> getPluginsByScene(String sceneType) {
        return plugins.values().stream()
                .filter(p -> p.getSceneType().equals(sceneType))
                .toList();
    }

    public MethodologyResult calculate(String code, Map<String, Object> parameters) {
        MethodologyPlugin plugin = getPlugin(code);
        if (plugin == null) {
            throw new IllegalArgumentException("方法学插件不存在: " + code);
        }
        return plugin.calculate(parameters);
    }
}
